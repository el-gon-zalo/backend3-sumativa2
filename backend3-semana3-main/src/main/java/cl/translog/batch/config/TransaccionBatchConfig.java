package cl.translog.batch.config;

import cl.translog.batch.domain.Transaccion;
import cl.translog.batch.exception.TemporaryTransaccionException;
import cl.translog.batch.listener.JobCompletionListener;
import cl.translog.batch.listener.TransaccionSkipListener;
import cl.translog.batch.partition.EntityPartitioner;
import cl.translog.batch.policy.TransaccionSkipPolicy;
import cl.translog.batch.processor.TransaccionProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.RetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.dao.CannotAcquireLockException;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TransaccionBatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<Transaccion> transaccionReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        int safeStart = start == null ? 0 : start;
        int safeEnd = end == null ? 0 : end;

        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader-" + safeStart + "-" + safeEnd)
                .resource(new ClassPathResource("input/transacciones.csv"))
                .linesToSkip(1)
                .currentItemCount(safeStart)
                .maxItemCount(safeEnd + 1)
                .delimited()
                .names(
                        "id",
                        "fecha",
                        "monto",
                        "tipo"
                )
                .fieldSetMapper(fieldSet -> {

                    Transaccion transaccion = new Transaccion();

                    transaccion.setId(
                            fieldSet.readInt("id")
                    );

                    transaccion.setFecha(
                            fieldSet.readString("fecha")
                    );

                    transaccion.setMonto(
                            fieldSet.readDouble("monto")
                    );

                    transaccion.setTipo(
                            fieldSet.readString("tipo")
                    );

                    return transaccion;
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaccion> transaccionWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Transaccion>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO transaccion_processed
                        (
                        id,
                        fecha,
                        monto,
                        tipo,
                        estado
                        )
                        VALUES
                        (
                        :id,
                        :fecha,
                        :monto,
                        :tipo,
                        :estado
                        )
                        ON DUPLICATE KEY UPDATE
                        fecha = VALUES(fecha),
                        monto = VALUES(monto),
                        tipo = VALUES(tipo),
                        estado = VALUES(estado)
                        """)
                .beanMapped()
                .build();
    }

        @Bean
        public RetryPolicy transaccionRetryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptions =
                new HashMap<>();

        exceptions.put(
                TemporaryTransaccionException.class,
                true
        );

        exceptions.put(
                org.springframework.dao.CannotAcquireLockException.class,
                true
        );

        return new SimpleRetryPolicy(
                6,
                exceptions,
                true
        );
        }

    @Bean
    public BackOffPolicy transaccionBackOffPolicy() {

        org.springframework.retry.backoff.ExponentialRandomBackOffPolicy policy =
            new org.springframework.retry.backoff.ExponentialRandomBackOffPolicy();

        policy.setInitialInterval(500);
        policy.setMultiplier(2);
        policy.setMaxInterval(4000);

        return policy;
    }

    @Bean
    public Step transaccionWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> transaccionReader,
            TransaccionProcessor transaccionProcessor,
            JdbcBatchItemWriter<Transaccion> transaccionWriter,
            TransaccionSkipPolicy transaccionSkipPolicy,
            TransaccionSkipListener transaccionSkipListener,
            RetryPolicy transaccionRetryPolicy,
            BackOffPolicy transaccionBackOffPolicy,
            @Value("${app.chunk-size}") int chunkSize) {

        return new StepBuilder(
                "transaccionWorkerStep",
                jobRepository
        )
                .<Transaccion, Transaccion>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                .faultTolerant()
                .skipPolicy(transaccionSkipPolicy)
                .retryPolicy(transaccionRetryPolicy)
                .backOffPolicy(transaccionBackOffPolicy)
                .listener(transaccionSkipListener)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler transaccionPartitionHandler(
            Step transaccionWorkerStep,
            TaskExecutor taskExecutor,
            @Value("${app.grid-size}") int gridSize) {

        TaskExecutorPartitionHandler handler =
                new TaskExecutorPartitionHandler();

        handler.setStep(transaccionWorkerStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);

        return handler;
    }

    @Bean
    public Step transaccionPartitionStep(
            JobRepository jobRepository,
            EntityPartitioner partitioner,
            TaskExecutorPartitionHandler transaccionPartitionHandler) {

        return new StepBuilder(
                "transaccionPartitionStep",
                jobRepository
        )
                .partitioner(
                        "transaccionWorkerStep",
                        partitioner
                )
                .partitionHandler(
                        transaccionPartitionHandler
                )
                .build();
    }

        @Bean
        public Step transaccionReportStep(
                JobRepository jobRepository,
                PlatformTransactionManager transactionManager,
                DataSource dataSource,
                @Value("${app.output-file-transacciones}") String outputFile) {

        return new StepBuilder(
                "transaccionReportStep",
                jobRepository
        )
                .tasklet(
                        (contribution, chunkContext) -> {

                                JdbcTemplate jdbc =
                                        new JdbcTemplate(dataSource);

                                List<Map<String, Object>> rows =
                                        jdbc.queryForList("""
                                                SELECT id, fecha, monto, tipo, estado
                                                FROM transaccion_processed
                                                """);

                                Path path =
                                        Paths.get(outputFile);

                                Path parent = path.getParent();

                                if (parent != null) {
                                Files.createDirectories(parent);
                                }

                                try (BufferedWriter writer =
                                        Files.newBufferedWriter(path)) {

                                writer.write(
                                        "id,fecha,monto,tipo,estado"
                                );
                                writer.newLine();

                                for (Map<String, Object> row : rows) {
                                        writer.write(
                                                row.get("id") + ","
                                                        + row.get("fecha") + ","
                                                        + row.get("monto") + ","
                                                        + row.get("tipo") + ","
                                                        + row.get("estado")
                                        );
                                        writer.newLine();
                                }
                                }

                                System.out.println(
                                        "Reporte generado en: "
                                                + path.toAbsolutePath()
                                );

                                return RepeatStatus.FINISHED;
                        },
                        transactionManager
                )
                .build();
        }

    @Bean
    public Job procesarTransaccionesJob(
            JobRepository jobRepository,
            Step transaccionPartitionStep,
            Step transaccionReportStep,
            JobCompletionListener listener) {

        return new JobBuilder(
                "procesarTransaccionesJob",
                jobRepository
        )
                .listener(listener)
                .start(transaccionPartitionStep)
                .next(transaccionReportStep)
                .build();
    }
}

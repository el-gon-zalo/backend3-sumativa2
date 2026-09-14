package cl.translog.batch.config;

import cl.translog.batch.domain.CuentaAnual;
import cl.translog.batch.exception.TemporaryCuentaAnualException;
import cl.translog.batch.listener.JobCompletionListener;
import cl.translog.batch.listener.CuentaAnualSkipListener;
import cl.translog.batch.partition.EntityPartitioner;
import cl.translog.batch.policy.CuentaAnualSkipPolicy;
import cl.translog.batch.processor.CuentaAnualProcessor;

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
public class CuentaAnualBatchConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<CuentaAnual> cuentaAnualReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        int safeStart = start == null ? 0 : start;
        int safeEnd = end == null ? 0 : end;

        return new FlatFileItemReaderBuilder<CuentaAnual>()
                .name("cuentaAnualReader-" + safeStart + "-" + safeEnd)
                .resource(new ClassPathResource("input/cuentas_anuales.csv"))
                .linesToSkip(1)
                .currentItemCount(safeStart)
                .maxItemCount(safeEnd + 1)
                .delimited()
                .names(
                        "cuenta_id",
                        "fecha",
                        "transaccion",
                        "monto",
                        "descripcion"
                )
                .fieldSetMapper(fieldSet -> {

                    CuentaAnual cuentaAnual = new CuentaAnual();

                    cuentaAnual.setCuentaId(
                            fieldSet.readInt("cuenta_id")
                    );

                    cuentaAnual.setFecha(
                            fieldSet.readString("fecha")
                    );

                    cuentaAnual.setTransaccion(
                            fieldSet.readString("transaccion")
                    );

                    cuentaAnual.setMonto(
                            fieldSet.readDouble("monto")
                    );

                    cuentaAnual.setDescripcion(
                            fieldSet.readString("descripcion")
                    );

                    return cuentaAnual;
                })
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CuentaAnual> cuentaAnualWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CuentaAnual>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO cuentaAnual_processed
                        (
                            cuenta_id,
                            fecha,
                            transaccion,
                            monto,
                            descripcion
                        )
                        VALUES
                        (
                            :cuentaId,
                            :fecha,
                            :transaccion,
                            :monto,
                            :descripcion
                        )
                        """)
                .beanMapped()
                .build();
    }

        @Bean
        public RetryPolicy cuentaAnualRetryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptions =
                new HashMap<>();

        exceptions.put(
                TemporaryCuentaAnualException.class,
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
        public BackOffPolicy cuentaAnualBackOffPolicy() {

        org.springframework.retry.backoff.ExponentialRandomBackOffPolicy policy =
                new org.springframework.retry.backoff.ExponentialRandomBackOffPolicy();

        policy.setInitialInterval(500);
        policy.setMultiplier(2);
        policy.setMaxInterval(4000);

        return policy;
        }

    @Bean
    public Step cuentaAnualWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<CuentaAnual> cuentaAnualReader,
            CuentaAnualProcessor cuentaAnualProcessor,
            JdbcBatchItemWriter<CuentaAnual> cuentaAnualWriter,
            CuentaAnualSkipPolicy cuentaAnualSkipPolicy,
            CuentaAnualSkipListener cuentaAnualSkipListener,
            RetryPolicy cuentaAnualRetryPolicy,
            BackOffPolicy cuentaAnualBackOffPolicy,
            @Value("${app.chunk-size}") int chunkSize) {

        return new StepBuilder(
                "cuentaAnualWorkerStep",
                jobRepository
        )
                .<CuentaAnual, CuentaAnual>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(cuentaAnualReader)
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter)
                .faultTolerant()
                .skipPolicy(cuentaAnualSkipPolicy)
                .retryPolicy(cuentaAnualRetryPolicy)
                .backOffPolicy(cuentaAnualBackOffPolicy)
                .listener(cuentaAnualSkipListener)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler cuentaAnualPartitionHandler(
            Step cuentaAnualWorkerStep,
            TaskExecutor taskExecutor,
            @Value("${app.grid-size}") int gridSize) {

        TaskExecutorPartitionHandler handler =
                new TaskExecutorPartitionHandler();

        handler.setStep(cuentaAnualWorkerStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(gridSize);

        return handler;
    }

    @Bean
    public Step partitionStep(
            JobRepository jobRepository,
            EntityPartitioner partitioner,
            TaskExecutorPartitionHandler cuentaAnualPartitionHandler) {

        return new StepBuilder(
                "partitionStep",
                jobRepository
        )
                .partitioner(
                        "cuentaAnualWorkerStep",
                        partitioner
                )
                .partitionHandler(
                        cuentaAnualPartitionHandler
                )
                .build();
    }

        @Bean
        public Step cuentaAnualReportStep(
                JobRepository jobRepository,
                PlatformTransactionManager transactionManager,
                DataSource dataSource,
                @Value("${app.output-file-cuentas-anuales}") String outputFile) {

        return new StepBuilder(
                "cuentaAnualReportStep",
                jobRepository
        )
                .tasklet(
                        (contribution, chunkContext) -> {

                                JdbcTemplate jdbc =
                                        new JdbcTemplate(dataSource);

                                List<Map<String, Object>> rows =
                                        jdbc.queryForList("""
                                                SELECT
                                                cuenta_id,
                                                fecha,
                                                transaccion,
                                                monto,
                                                descripcion
                                                FROM cuentaAnual_processed
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
                                        "cuenta_id,fecha,transaccion,monto,descripcion"
                                );
                                writer.newLine();

                                for (Map<String, Object> row : rows) {
                                        writer.write(
                                                row.get("cuenta_id") + ","
                                                        + row.get("fecha") + ","
                                                        + row.get("transaccion") + ","
                                                        + row.get("monto") + ","
                                                        + row.get("descripcion")
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
    public Job procesarCuentaAnualJob(
            JobRepository jobRepository,
            Step partitionStep,
            Step cuentaAnualReportStep,
            JobCompletionListener listener) {

        return new JobBuilder(
                "procesarCuentaAnualJob",
                jobRepository
        )
                .listener(listener)
                .start(partitionStep)
                .next(cuentaAnualReportStep)
                .build();
    }
}

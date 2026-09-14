package cl.translog.batch.processor;

import cl.translog.batch.domain.Transaccion;
import cl.translog.batch.exception.InvalidTransaccionException;
import cl.translog.batch.exception.TemporaryTransaccionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransaccionProcessor implements ItemProcessor<Transaccion, Transaccion> {

    private static final Logger log =
            LoggerFactory.getLogger(TransaccionProcessor.class);

    // Formatos de fecha
    private static final DateTimeFormatter FORMATO_SALIDA =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter FORMATO_DD_MM_GUION =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter FORMATO_DD_MM_SLASH =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_YYYY_MM_SLASH =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter FORMATO_YYYY_MM_GUION =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Set<Long> temporaryFailures =
            ConcurrentHashMap.newKeySet();


    @Override
    public Transaccion process(Transaccion transaccion) {

        log.info(
                "[{}] Procesando transaccion {}",
                Thread.currentThread().getName(),
                transaccion.getId()
        );

        // Casos borde - Manejo de excepciones
        if (transaccion.getId() == null) {

            throw new InvalidTransaccionException(
                    "Existe Id invalido"
            );
        }

        if (transaccion.getFecha() == null
                || transaccion.getFecha().trim().isEmpty()) {

            throw new InvalidTransaccionException(
                    "Fecha invalida para transaccion "
                            + transaccion.getId()
            );
        }

        if (transaccion.getMonto() == null || transaccion.getMonto() < 0) {

            throw new InvalidTransaccionException(
                    "Monto invalido para transaccion "
                            + transaccion.getId()
            );
        }

        // Estado: detectar anomalia
        if (transaccion.getMonto() > 1000000) {
            transaccion.setEstado("MONTO_ANOMALO");
        } else {
            transaccion.setEstado("MONTO_NORMAL");
        }


        // FORMATEO DE FECHA
        String fechaFormateada =
                formatearFecha(transaccion.getFecha());

        transaccion.setFecha(fechaFormateada);


        return transaccion;
    }


    private static String formatearFecha(String fecha) {

        if (fecha == null || fecha.trim().isEmpty()) {
            return "";
        }

        String valor = fecha.trim();

        LocalDate fechaNormalizada;

        if (valor.matches("\\d{2}-\\d{2}-\\d{4}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_DD_MM_GUION);

        } else if (valor.matches("\\d{2}/\\d{2}/\\d{4}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_DD_MM_SLASH);

        } else if (valor.matches("\\d{4}/\\d{2}/\\d{2}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_YYYY_MM_SLASH);

        } else if (valor.matches("\\d{4}-\\d{2}-\\d{2}")) {

            fechaNormalizada =
                    LocalDate.parse(valor, FORMATO_YYYY_MM_GUION);

        } else {

            throw new IllegalArgumentException(
                    "Formato de fecha no reconocido: " + valor
            );
        }

        return fechaNormalizada.format(FORMATO_SALIDA);
    }
}
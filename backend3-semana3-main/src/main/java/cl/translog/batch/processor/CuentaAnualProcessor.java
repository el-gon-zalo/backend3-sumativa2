package cl.translog.batch.processor;

import cl.translog.batch.domain.CuentaAnual;
import cl.translog.batch.exception.InvalidCuentaAnualException;
import cl.translog.batch.exception.TemporaryCuentaAnualException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnual, CuentaAnual> {

    private static final Logger log =
            LoggerFactory.getLogger(CuentaAnualProcessor.class);

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
    public CuentaAnual process(CuentaAnual cuentaAnual) {

        log.info(
                "[{}] Procesando cuentaAnual {}",
                Thread.currentThread().getName(),
                cuentaAnual.getCuentaId()
        );

        // Casos borde - Manejo de excepciones
        if (cuentaAnual.getCuentaId() == null) {

            throw new InvalidCuentaAnualException(
                    "Existe Id invalido"
            );
        }

        if (cuentaAnual.getFecha() == null
                || cuentaAnual.getFecha().trim().isEmpty()) {

            throw new InvalidCuentaAnualException(
                    "Fecha invalida para cuentaAnual "
                            + cuentaAnual.getCuentaId()
            );
        }

        if (cuentaAnual.getMonto() == null) {

            throw new InvalidCuentaAnualException(
                    "Monto invalido para cuentaAnual "
                            + cuentaAnual.getCuentaId()
            );
        }


        // FORMATEO DE FECHA
        String fechaFormateada =
                formatearFecha(cuentaAnual.getFecha());

        cuentaAnual.setFecha(fechaFormateada);


        return cuentaAnual;
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
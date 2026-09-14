package cl.translog.bff.cajero.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Map;



@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RestClientException.class)
    ResponseEntity<Map<String, Object>> coreUnavailable(RestClientException ex) {

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 502,
                "error", "BAD_GATEWAY",
                "message", ex.getMessage()
        ));
    }

    /* CAMBIO TEMPORAL
    @ExceptionHandler(RestClientException.class)
    ResponseEntity<Map<String, Object>> coreUnavailable(RestClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 502,
                "error", "BAD_GATEWAY",
                "message", "El BFF no pudo obtener una respuesta válida desde CORE"
        ));
    }
    */


    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> unexpected(Exception ex) {

        log.error("Error inesperado en BFF", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "INTERNAL_ERROR",
                "message", "No fue posible procesar la solicitud"
        ));
    }

    /*@ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> unexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "INTERNAL_ERROR",
                "message", "No fue posible procesar la solicitud"
        ));
    }
        
    */
}

package cl.translog.batch.exception;

public class TemporaryTransaccionException extends RuntimeException {

    public TemporaryTransaccionException(String message) {
        super(message);
    }
}

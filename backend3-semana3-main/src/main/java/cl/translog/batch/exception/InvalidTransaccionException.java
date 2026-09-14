package cl.translog.batch.exception;

public class InvalidTransaccionException extends RuntimeException {

    public InvalidTransaccionException(String message) {
        super(message);
    }
}

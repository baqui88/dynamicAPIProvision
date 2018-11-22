package csc.web.exception;

public class DatabaseNotSupportedException extends RuntimeException {
    public DatabaseNotSupportedException(String message) {
        super(message);
    }
}

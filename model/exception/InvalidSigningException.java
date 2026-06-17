package model.exception;

public class InvalidSigningException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidSigningException(String message) {
        super(message);
    }
}

package model.exception;

public class UnauthorisedAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnauthorisedAccessException(String message) {
        super(message);
    }
}

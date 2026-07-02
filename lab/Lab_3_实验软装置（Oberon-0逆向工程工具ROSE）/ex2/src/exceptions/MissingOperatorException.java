package exceptions;

/**
 * Thrown when a required operator is missing or a predefined function
 * is called with missing parameters.
 */
public class MissingOperatorException extends SyntacticException {
    public MissingOperatorException(String message) {
        super(message);
    }
}

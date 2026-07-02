package exceptions;

/**
 * Thrown when a required operand is missing.
 */
public class MissingOperandException extends SyntacticException {
    public MissingOperandException(String message) {
        super(message);
    }
}

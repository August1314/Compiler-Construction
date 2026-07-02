package exceptions;

/**
 * Thrown when the number of actual parameters in a procedure call
 * does not match the number of formal parameters in the declaration.
 */
public class ParameterMismatchedException extends SemanticException {
    public ParameterMismatchedException(String message) {
        super(message);
    }
}

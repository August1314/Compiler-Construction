package exceptions;

/**
 * Thrown when a type compatibility error is detected in expressions,
 * assignments, or parameter passing.
 */
public class TypeMismatchedException extends SemanticException {
    public TypeMismatchedException(String message) {
        super(message);
    }
}

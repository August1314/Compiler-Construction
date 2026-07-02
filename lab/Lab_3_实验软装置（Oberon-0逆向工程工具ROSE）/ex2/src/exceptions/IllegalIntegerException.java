package exceptions;

/**
 * Thrown when an integer constant has no blank separator before a following identifier.
 */
public class IllegalIntegerException extends LexicalException {
    public IllegalIntegerException(String message) {
        super(message);
    }
}

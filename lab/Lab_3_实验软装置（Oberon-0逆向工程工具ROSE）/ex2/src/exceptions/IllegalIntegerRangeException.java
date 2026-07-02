package exceptions;

/**
 * Thrown when an integer constant exceeds the maximum 12-digit range.
 */
public class IllegalIntegerRangeException extends LexicalException {
    public IllegalIntegerRangeException(String message) {
        super(message);
    }
}

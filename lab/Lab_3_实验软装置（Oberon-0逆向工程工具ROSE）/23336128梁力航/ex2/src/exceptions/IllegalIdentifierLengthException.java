package exceptions;

/**
 * Thrown when an identifier exceeds the maximum 24-character length.
 */
public class IllegalIdentifierLengthException extends LexicalException {
    public IllegalIdentifierLengthException(String message) {
        super(message);
    }
}

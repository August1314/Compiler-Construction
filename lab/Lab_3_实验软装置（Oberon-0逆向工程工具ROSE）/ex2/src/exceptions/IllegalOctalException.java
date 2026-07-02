package exceptions;

/**
 * Thrown when an octal constant contains digits 8 or 9.
 */
public class IllegalOctalException extends LexicalException {
    public IllegalOctalException(String message) {
        super(message);
    }
}

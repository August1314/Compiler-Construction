package exceptions;

/**
 * Thrown when an illegal symbol (e.g. @, $) is encountered during scanning.
 */
public class IllegalSymbolException extends LexicalException {
    public IllegalSymbolException(String message) {
        super(message);
    }
}

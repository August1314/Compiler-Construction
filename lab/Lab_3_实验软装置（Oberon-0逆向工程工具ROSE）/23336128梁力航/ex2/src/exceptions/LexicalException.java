package exceptions;

/**
 * Represents all lexical errors found during scanning.
 *
 * @author Lianglihang
 * @version 1.00
 */
public class LexicalException extends OberonException {
    public LexicalException() {
        super();
    }

    public LexicalException(String message) {
        super(message);
    }
}

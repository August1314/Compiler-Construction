package exceptions;

/**
 * Represents all syntactic errors found during parsing.
 *
 * @author Lianglihang
 * @version 1.00
 */
public class SyntacticException extends OberonException {
    public SyntacticException() {
        super();
    }

    public SyntacticException(String message) {
        super(message);
    }
}

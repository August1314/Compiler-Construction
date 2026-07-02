package exceptions;

/**
 * Represents all semantic errors found during analysis.
 *
 * @author Lianglihang
 * @version 1.00
 */
public class SemanticException extends OberonException {
    public SemanticException() {
        super();
    }

    public SemanticException(String message) {
        super(message);
    }
}

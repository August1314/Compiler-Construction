package exceptions;

/**
 * Root class for all exceptions thrown during Oberon-0 program analysis.
 *
 * @author Lianglihang
 * @version 1.00
 */
public class OberonException extends Exception {
    public OberonException() {
        super();
    }

    public OberonException(String message) {
        super(message);
    }
}

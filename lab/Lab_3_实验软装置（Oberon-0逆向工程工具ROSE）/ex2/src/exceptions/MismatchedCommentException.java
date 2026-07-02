package exceptions;

/**
 * Thrown when a comment opened with (* is never closed.
 */
public class MismatchedCommentException extends LexicalException {
    public MismatchedCommentException(String message) {
        super(message);
    }
}

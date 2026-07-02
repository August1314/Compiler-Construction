package exceptions;

/**
 * Thrown when left and right parentheses are mismatched and a left parenthesis is missing.
 */
public class MissingLeftParenthesisException extends SyntacticException {
    public MissingLeftParenthesisException(String message) {
        super(message);
    }
}

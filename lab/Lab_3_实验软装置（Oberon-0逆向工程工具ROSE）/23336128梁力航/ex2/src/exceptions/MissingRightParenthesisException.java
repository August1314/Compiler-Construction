package exceptions;

/**
 * Thrown when left and right parentheses are mismatched and a right parenthesis is missing.
 */
public class MissingRightParenthesisException extends SyntacticException {
    public MissingRightParenthesisException(String message) {
        super(message);
    }
}

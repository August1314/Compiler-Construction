import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for a simple infix expression language with digits and +/-. It outputs
 * the equivalent postfix notation.
 */
class Parser {
	private final Reader reader;
	private final boolean recover;
	private int lookahead;
	private int position;
	private final StringBuilder output;
	private final List<ParseError> errors;

	/**
	 * Constructs a parser that reads from standard input with recovery disabled.
	 *
	 * @throws IOException if input cannot be read
	 */
	public Parser() throws IOException {
		this(new InputStreamReader(System.in), false);
	}

	/**
	 * Constructs a parser that reads from standard input.
	 *
	 * @param recover whether to continue after errors
	 * @throws IOException if input cannot be read
	 */
	public Parser(boolean recover) throws IOException {
		this(new InputStreamReader(System.in), recover);
	}

	/**
	 * Constructs a parser with a custom reader.
	 *
	 * @param reader  input source
	 * @param recover whether to continue after errors
	 * @throws IOException if input cannot be read
	 */
	public Parser(Reader reader, boolean recover) throws IOException {
		this.reader = reader;
		this.recover = recover;
		this.output = new StringBuilder();
		this.errors = new ArrayList<ParseError>();
		this.position = 0;
		read();
	}

	/**
	 * Parses the input and returns the postfix result plus any errors.
	 *
	 * @return parse result
	 * @throws IOException if input cannot be read
	 */
	public ParseResult parse() throws IOException {
		parseExpr();
		return new ParseResult(output.toString(), errors);
	}

	private void parseExpr() throws IOException {
		boolean expectOperand = true;
		boolean sawAny = false;
		char pendingOp = 0;
		while (true) {
			if (isEnd(lookahead)) {
				if (!sawAny) {
					addSyntaxError("empty expression");
				} else if (expectOperand) {
					addSyntaxError("missing right operand");
				}
				break;
			}

			if (expectOperand) {
				if (isDigit(lookahead)) {
					appendOutput((char) lookahead);
					sawAny = true;
					read();
					if (pendingOp != 0) {
						appendOutput(pendingOp);
						pendingOp = 0;
					}
					expectOperand = false;
					continue;
				}
				if (isOperator(lookahead)) {
					addSyntaxError("missing left operand");
					if (!recover) {
						break;
					}
					read();
					continue;
				}
				if (isWhitespace(lookahead)) {
					addLexicalError("whitespace not allowed");
					if (!recover) {
						break;
					}
					read();
					continue;
				}
				addLexicalError("illegal character '" + (char) lookahead + "'");
				if (!recover) {
					break;
				}
				read();
				continue;
			}

			if (isOperator(lookahead)) {
				pendingOp = (char) lookahead;
				read();
				expectOperand = true;
				continue;
			}
			if (isDigit(lookahead)) {
				addSyntaxError("missing operator");
				if (!recover) {
					break;
				}
				read();
				continue;
			}
			if (isWhitespace(lookahead)) {
				addLexicalError("whitespace not allowed");
				if (!recover) {
					break;
				}
				read();
				continue;
			}
			addLexicalError("illegal character '" + (char) lookahead + "'");
			if (!recover) {
				break;
			}
			read();
		}
	}

	private void appendOutput(char ch) {
		if (errors.isEmpty()) {
			output.append(ch);
		}
	}

	private void addSyntaxError(String message) {
		errors.add(new ParseError(ErrorKind.SYNTAX, errorPosition(), message));
	}

	private void addLexicalError(String message) {
		errors.add(new ParseError(ErrorKind.LEXICAL, errorPosition(), message));
	}

	private int errorPosition() {
		if (lookahead == -1) {
			return position + 1;
		}
		return position;
	}

	private void read() throws IOException {
		lookahead = reader.read();
		if (lookahead != -1) {
			position++;
		}
	}

	private static boolean isEnd(int c) {
		return c == -1 || c == '\n' || c == '\r';
	}

	private static boolean isOperator(int c) {
		return c == '+' || c == '-';
	}

	private static boolean isWhitespace(int c) {
		return c == ' ' || c == '\t' || c == '\f';
	}

	private static boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}
}

/**
 * Error kinds reported by the parser.
 */
enum ErrorKind {
	LEXICAL,
	SYNTAX
}

/**
 * Represents a lexical or syntax error with location info.
 */
class ParseError {
	private final ErrorKind kind;
	private final int position;
	private final String message;

	public ParseError(ErrorKind kind, int position, String message) {
		this.kind = kind;
		this.position = position;
		this.message = message;
	}

	public ErrorKind getKind() {
		return kind;
	}

	public int getPosition() {
		return position;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Returns a formatted error message string.
	 *
	 * @return formatted error string
	 */
	public String format() {
		return kind + " error at position " + position + ": " + message;
	}
}

/**
 * Parse result with postfix output and errors.
 */
class ParseResult {
	private final String postfix;
	private final List<ParseError> errors;

	public ParseResult(String postfix, List<ParseError> errors) {
		this.postfix = postfix;
		this.errors = errors;
	}

	public String getPostfix() {
		return postfix;
	}

	public List<ParseError> getErrors() {
		return errors;
	}

	/**
	 * @return true if any errors were recorded
	 */
	public boolean hasErrors() {
		return errors != null && !errors.isEmpty();
	}
}

/**
 * Program entry point for postfix conversion.
 */
public class Postfix {
	public static void main(String[] args) throws IOException {
		boolean recover = false;
		boolean quiet = false;
		for (int i = 0; i < args.length; i++) {
			if ("--recover".equals(args[i])) {
				recover = true;
			} else if ("--quiet".equals(args[i])) {
				quiet = true;
			}
		}

		if (!quiet) {
			System.out.println("Input an infix expression and output its postfix notation:");
		}
		Parser parser = new Parser(new InputStreamReader(System.in), recover);
		ParseResult result = parser.parse();
		System.out.print(result.getPostfix());
		if (result.hasErrors()) {
			System.out.print(" (error)");
		}
		if (quiet) {
			System.out.println();
		} else {
			System.out.println("\nEnd of program.");
		}

		if (result.hasErrors()) {
			for (int i = 0; i < result.getErrors().size(); i++) {
				System.err.println(result.getErrors().get(i).format());
			}
		}
	}
}

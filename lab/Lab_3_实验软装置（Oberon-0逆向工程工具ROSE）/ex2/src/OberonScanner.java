/**
 * Oberon-0 language lexical scanner.
 *
 * <p>This scanner reads an Oberon-0 source program character by character and
 * produces a stream of tokens. It detects lexical errors and throws the
 * appropriate exception from the exceptions package.
 *
 * <p>Key specifications:
 * <ul>
 *   <li>Case-insensitive: WHILE, While, while are equivalent</li>
 *   <li>Identifiers: max 24 chars, letter{letter|digit}</li>
 *   <li>Integer constants: decimal (non-0 start) or octal (0 start), max 12 digits</li>
 *   <li>Comments: (* ... *), no nesting</li>
 *   <li>Separation: integer constants must be separated from identifiers by whitespace</li>
 * </ul>
 *
 * @author Lianglihang
 * @version 1.00
 */
package oberon;

import exceptions.*;
import java.io.*;

public class OberonScanner {
    private final Reader reader;
    private int currentChar;
    private int line;
    private int column;
    private Token currentToken;
    private boolean hasPushedBack;

    /** Token type constants for standalone use. */
    public static final int
        EOF_TOKEN = 0,
        MODULE = 1,
        BEGIN = 2,
        END = 3,
        CONST = 4,
        TYPE = 5,
        VAR = 6,
        PROCEDURE = 7,
        IF = 8,
        THEN = 9,
        ELSIF = 10,
        ELSE = 11,
        WHILE = 12,
        DO = 13,
        ARRAY = 14,
        OF = 15,
        RECORD = 16,
        DIV = 17,
        MODOP = 18,
        OR = 19,
        READ = 20,
        WRITE = 21,
        WRITELN = 22,
        IDENTIFIER = 23,
        INTEGER_LITERAL = 24,
        ASSIGN = 25,
        EQ = 26,
        NE = 27,
        LT = 28,
        LE = 29,
        GT = 30,
        GE = 31,
        PLUS = 32,
        MINUS = 33,
        STAR = 34,
        AND = 35,
        NOT = 36,
        SEMICOLON = 37,
        DOT = 38,
        COMMA = 39,
        LPAREN = 40,
        RPAREN = 41,
        LBRACKET = 42,
        RBRACKET = 43,
        COLON = 44;

    /** Token class: pair of type and optional semantic value. */
    public static class Token {
        public final int type;
        public final Object value;
        public final int line;
        public final int column;

        public Token(int type, Object value, int line, int column) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return "Token{" + type + ", " + value + "} @" + line + ":" + column;
        }
    }

    public OberonScanner(Reader reader) throws IOException {
        this.reader = reader;
        this.line = 1;
        this.column = 1;
        this.currentToken = null;
        this.hasPushedBack = false;
        advance();
    }

    public OberonScanner(String input) throws IOException {
        this(new StringReader(input));
    }

    /** Returns the current line number (1-based). */
    public int getLine() { return line; }

    /** Returns the current column number (1-based). */
    public int getColumn() { return column; }

    /** Reads the next token without consuming it. */
    public Token peek() throws OberonException, IOException {
        if (currentToken == null) {
            currentToken = readToken();
        }
        return currentToken;
    }

    /** Reads and consumes the next token. */
    public Token next() throws OberonException, IOException {
        Token token = peek();
        currentToken = null;
        return token;
    }

    // --- Internal scanner implementation ---

    private void advance() throws IOException {
        currentChar = reader.read();
        column++;
        if (currentChar == '\n') {
            line++;
            column = 1;
        }
    }

    private void skipWhitespace() throws IOException {
        while (currentChar != -1 && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private Token readToken() throws OberonException, IOException {
        skipWhitespace();

        if (currentChar == -1) {
            return new Token(EOF_TOKEN, null, line, column);
        }

        int startLine = line;
        int startCol = column;
        char ch = (char) currentChar;

        // Comment: (* ... *)
        if (ch == '(') {
            advance();
            if (currentChar == '*') {
                advance();
                skipComment();
                return readToken(); // skip comment, read next token
            }
            return new Token(LPAREN, "(", startLine, startCol);
        }

        // Numbers
        if (Character.isDigit(ch)) {
            return readNumber(startLine, startCol);
        }

        // Identifiers and reserved words
        if (Character.isLetter(ch)) {
            return readIdentifierOrReservedWord(startLine, startCol);
        }

        // Single/double character operators and delimiters
        switch (ch) {
            case ':':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(ASSIGN, ":=", startLine, startCol);
                }
                return new Token(COLON, ":", startLine, startCol);
            case '=': advance(); return new Token(EQ, "=", startLine, startCol);
            case '#': advance(); return new Token(NE, "#", startLine, startCol);
            case '<':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(LE, "<=", startLine, startCol);
                }
                return new Token(LT, "<", startLine, startCol);
            case '>':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(GE, ">=", startLine, startCol);
                }
                return new Token(GT, ">", startLine, startCol);
            case '+': advance(); return new Token(PLUS, "+", startLine, startCol);
            case '-': advance(); return new Token(MINUS, "-", startLine, startCol);
            case '*': advance(); return new Token(STAR, "*", startLine, startCol);
            case '&': advance(); return new Token(AND, "&", startLine, startCol);
            case '~': advance(); return new Token(NOT, "~", startLine, startCol);
            case ';': advance(); return new Token(SEMICOLON, ";", startLine, startCol);
            case '.': advance(); return new Token(DOT, ".", startLine, startCol);
            case ',': advance(); return new Token(COMMA, ",", startLine, startCol);
            case '[': advance(); return new Token(LBRACKET, "[", startLine, startCol);
            case ']': advance(); return new Token(RBRACKET, "]", startLine, startCol);
            case ')': advance(); return new Token(RPAREN, ")", startLine, startCol);
            default:
                advance();
                throw new IllegalSymbolException(
                    "Illegal symbol '" + ch + "' at line " + startLine + ", column " + startCol);
        }
    }

    private void skipComment() throws IOException, MismatchedCommentException {
        int startLine = line;
        int startCol = column - 2; // backtrack to (*
        while (true) {
            if (currentChar == -1) {
                throw new MismatchedCommentException(
                    "Unclosed comment starting at line " + startLine + ", column " + startCol);
            }
            if (currentChar == '*') {
                advance();
                if (currentChar == ')') {
                    advance();
                    return;
                }
            } else {
                advance();
            }
        }
    }

    private Token readNumber(int startLine, int startCol) throws IOException, LexicalException {
        StringBuilder sb = new StringBuilder();
        boolean isOctal = (currentChar == '0');

        while (currentChar != -1 && Character.isDigit((char) currentChar)) {
            sb.append((char) currentChar);
            advance();
        }

        String text = sb.toString();

        // Check length limit (max 12 digits)
        if (text.length() > 12) {
            throw new IllegalIntegerRangeException(
                "Integer constant \"" + text + "\" exceeds maximum 12 digits " +
                "at line " + startLine + ", column " + startCol);
        }

        // Check for illegal concatenation with identifier
        if (currentChar != -1 && Character.isLetter((char) currentChar)) {
            throw new IllegalIntegerException(
                "Missing separator between integer \"" + text + "\" and following identifier " +
                "at line " + startLine + ", column " + startCol);
        }

        // Check octal validity
        if (isOctal && text.length() > 1) {
            for (int i = 1; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '8' || c == '9') {
                    throw new IllegalOctalException(
                        "Illegal octal digit '" + c + "' in constant \"" + text + "\" " +
                        "at line " + startLine + ", column " + startCol);
                }
            }
            return new Token(INTEGER_LITERAL, Integer.parseInt(text, 8), startLine, startCol);
        }

        return new Token(INTEGER_LITERAL, Integer.parseInt(text, 10), startLine, startCol);
    }

    private Token readIdentifierOrReservedWord(int startLine, int startCol)
            throws IOException, LexicalException {
        StringBuilder sb = new StringBuilder();

        while (currentChar != -1 &&
               (Character.isLetterOrDigit((char) currentChar))) {
            sb.append((char) currentChar);
            advance();
        }

        String text = sb.toString();

        // Check identifier length (max 24 characters)
        if (text.length() > 24) {
            throw new IllegalIdentifierLengthException(
                "Identifier \"" + text + "\" exceeds maximum length of 24 characters " +
                "at line " + startLine + ", column " + startCol);
        }

        // Case-insensitive reserved word / keyword lookup
        String lower = text.toLowerCase();
        switch (lower) {
            case "module":    return new Token(MODULE, lower, startLine, startCol);
            case "begin":     return new Token(BEGIN, lower, startLine, startCol);
            case "end":       return new Token(END, lower, startLine, startCol);
            case "const":     return new Token(CONST, lower, startLine, startCol);
            case "type":      return new Token(TYPE, lower, startLine, startCol);
            case "var":       return new Token(VAR, lower, startLine, startCol);
            case "procedure": return new Token(PROCEDURE, lower, startLine, startCol);
            case "if":        return new Token(IF, lower, startLine, startCol);
            case "then":      return new Token(THEN, lower, startLine, startCol);
            case "elsif":     return new Token(ELSIF, lower, startLine, startCol);
            case "else":      return new Token(ELSE, lower, startLine, startCol);
            case "while":     return new Token(WHILE, lower, startLine, startCol);
            case "do":        return new Token(DO, lower, startLine, startCol);
            case "array":     return new Token(ARRAY, lower, startLine, startCol);
            case "of":        return new Token(OF, lower, startLine, startCol);
            case "record":    return new Token(RECORD, lower, startLine, startCol);
            case "div":       return new Token(DIV, lower, startLine, startCol);
            case "mod":       return new Token(MODOP, lower, startLine, startCol);
            case "or":        return new Token(OR, lower, startLine, startCol);
            case "read":      return new Token(READ, lower, startLine, startCol);
            case "write":     return new Token(WRITE, lower, startLine, startCol);
            case "writeln":   return new Token(WRITELN, lower, startLine, startCol);
            // INTEGER, BOOLEAN, TRUE, FALSE are keywords (predefined identifiers)
            // in Oberon-0, NOT reserved words, so they are returned as IDENTIFIER
            default:          return new Token(IDENTIFIER, lower, startLine, startCol);
        }
    }

    // --- Main method for standalone testing (ex2) ---
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java oberon.OberonScanner <source-file>");
            System.exit(1);
        }

        try {
            OberonScanner scanner = new OberonScanner(new FileReader(args[0]));
            Token token;
            while ((token = scanner.next()).type != EOF_TOKEN) {
                System.out.println(token);
            }
        } catch (LexicalException e) {
            System.err.println("LEXICAL ERROR: " + e.getMessage());
            System.exit(1);
        } catch (OberonException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO ERROR: " + e.getMessage());
            System.exit(1);
        }
    }
}

/**
 * Token symbol constants for Oberon-0 language.
 * Aligned with OberonScanner token numbering.
 *
 * Note: INTEGER, BOOLEAN, TRUE, FALSE are keywords (predefined identifiers)
 * in Oberon-0, NOT reserved words. They are returned as IDENTIFIER tokens
 * by the scanner and recognized contextually by the parser.
 *
 * @author Lianglihang
 * @version 1.00
 */
public class sym {
    public static final int EOF = 0;
    public static final int error = 45;  // unused token for error recovery

    // Reserved words (matching OberonScanner numbering)
    public static final int MODULE = 1;
    public static final int BEGIN = 2;
    public static final int END = 3;
    public static final int CONST = 4;
    public static final int TYPE = 5;
    public static final int VAR = 6;
    public static final int PROCEDURE = 7;
    public static final int IF = 8;
    public static final int THEN = 9;
    public static final int ELSIF = 10;
    public static final int ELSE = 11;
    public static final int WHILE = 12;
    public static final int DO = 13;
    public static final int ARRAY = 14;
    public static final int OF = 15;
    public static final int RECORD = 16;
    public static final int DIV = 17;
    public static final int MODOP = 18;
    public static final int OR = 19;

    // Predefined procedures (keywords with special syntax: Read(x), Write(x), WriteLn)
    public static final int READ = 20;
    public static final int WRITE = 21;
    public static final int WRITELN = 22;

    // Identifiers and literals
    public static final int IDENTIFIER = 23;
    public static final int INTEGER_LITERAL = 24;

    // Operators
    public static final int ASSIGN = 25;
    public static final int EQ = 26;
    public static final int NE = 27;
    public static final int LT = 28;
    public static final int LE = 29;
    public static final int GT = 30;
    public static final int GE = 31;
    public static final int PLUS = 32;
    public static final int MINUS = 33;
    public static final int STAR = 34;
    public static final int AND = 35;
    public static final int NOT = 36;

    // Delimiters
    public static final int SEMICOLON = 37;
    public static final int DOT = 38;
    public static final int COMMA = 39;
    public static final int LPAREN = 40;
    public static final int RPAREN = 41;
    public static final int LBRACKET = 42;
    public static final int RBRACKET = 43;
    public static final int COLON = 44;

    /** Returns the name of a token type for debugging. */
    public static String nameOf(int type) {
        switch (type) {
            case EOF: return "EOF";
            case MODULE: return "MODULE";
            case BEGIN: return "BEGIN";
            case END: return "END";
            case CONST: return "CONST";
            case TYPE: return "TYPE";
            case VAR: return "VAR";
            case PROCEDURE: return "PROCEDURE";
            case IF: return "IF";
            case THEN: return "THEN";
            case ELSIF: return "ELSIF";
            case ELSE: return "ELSE";
            case WHILE: return "WHILE";
            case DO: return "DO";
            case ARRAY: return "ARRAY";
            case OF: return "OF";
            case RECORD: return "RECORD";
            case DIV: return "DIV";
            case MODOP: return "MODOP";
            case OR: return "OR";
            case READ: return "READ";
            case WRITE: return "WRITE";
            case WRITELN: return "WRITELN";
            case IDENTIFIER: return "IDENTIFIER";
            case INTEGER_LITERAL: return "INTEGER_LITERAL";
            case ASSIGN: return ":=";
            case EQ: return "=";
            case NE: return "#";
            case LT: return "<";
            case LE: return "<=";
            case GT: return ">";
            case GE: return ">=";
            case PLUS: return "+";
            case MINUS: return "-";
            case STAR: return "*";
            case AND: return "&";
            case NOT: return "~";
            case SEMICOLON: return ";";
            case DOT: return ".";
            case COMMA: return ",";
            case LPAREN: return "(";
            case RPAREN: return ")";
            case LBRACKET: return "[";
            case RBRACKET: return "]";
            case COLON: return ":";
            case error: return "error";
            default: return "UNKNOWN(" + type + ")";
        }
    }
}

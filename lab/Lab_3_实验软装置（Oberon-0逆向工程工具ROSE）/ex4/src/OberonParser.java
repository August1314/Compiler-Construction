/**
 * Oberon-0 recursive descent predictive parser with flowchart generation.
 *
 * <p>This parser implements a recursive descent predictive analysis for Oberon-0
 * programs. Each non-terminal in the grammar maps to a recursive method, and
 * the parser follows a translation scheme to generate flowcharts (Flowchart)
 * for each procedure in the module.
 *
 * <p>Design principles:
 * <ul>
 *   <li>Grammar is transformed to eliminate left recursion (suitable for top-down)</li>
 *   <li>Each non-terminal → one recursive method</li>
 *   <li>Inherited attributes → formal parameters</li>
 *   <li>Synthesized attributes → return values</li>
 *   <li>Translation scheme semantic actions embedded in parsing methods</li>
 * </ul>
 *
 * <p>Grammar transformations applied:
 * <ul>
 *   <li>declarations: left-recursive procedure_declaration list → iterative loop</li>
 *   <li>simple_expression: left-recursive term list → iterative loop</li>
 *   <li>term: left-recursive factor list → iterative loop</li>
 * </ul>
 *
 * @author Lianglihang
 * @version 1.00
 */
import flowchart.*;
import exceptions.*;
import oberon.OberonScanner;
import oberon.OberonScanner.Token;
import java.io.*;
import java.util.*;

public class OberonParser {
    private final OberonScanner scanner;
    private OberonScanner.Token lookahead;

    // --- Flowchart construction ---
    private flowchart.Module currentModule;
    private Procedure currentProcedure;
    private Stack<StatementSequence> seqStack;  // tracks current statement context
    private Stack<Object> controlStack;         // tracks IF/WHILE nesting

    // --- Semantic state ---
    private Map<String, String> typeEnvironment;

    public OberonParser(OberonScanner scanner) throws IOException, OberonException {
        this.scanner = scanner;
        this.lookahead = scanner.next();
        this.seqStack = new Stack<StatementSequence>();
        this.controlStack = new Stack<Object>();
        this.typeEnvironment = new HashMap<String, String>();
    }

    /**
     * Parse an Oberon-0 module and generate flowcharts for all procedures.
     */
    public void parse() throws OberonException, IOException {
        parseModule();
        currentModule.show();
    }

    // ===== Utilities =====

    private void match(int expectedType) throws OberonException, IOException {
        if (lookahead.type == expectedType) {
            lookahead = scanner.next();
        } else {
            error("Expected " + sym.nameOf(expectedType) +
                  " but got " + sym.nameOf(lookahead.type));
        }
    }

    private String consumeId() throws OberonException, IOException {
        if (lookahead.type == sym.IDENTIFIER) {
            String id = (String) lookahead.value;
            lookahead = scanner.next();
            return id;
        }
        error("Expected identifier but got " + sym.nameOf(lookahead.type));
        return null;
    }

    private int consumeInt() throws OberonException, IOException {
        if (lookahead.type == sym.INTEGER_LITERAL) {
            int val = (Integer) lookahead.value;
            lookahead = scanner.next();
            return val;
        }
        error("Expected integer literal");
        return 0;
    }

    private void error(String msg) throws OberonException {
        throw new SyntacticException(msg + " at line " + lookahead.line +
                                     ", column " + lookahead.column);
    }

    // ===================================================================
    // Translation Scheme for Oberon-0 (recursive descent)
    // ===================================================================

    //
    // module →
    //   "MODULE" identifier ";"
    //   declarations
    //   ["BEGIN" statement_sequence]
    //   "END" identifier "."
    //
    private void parseModule() throws OberonException, IOException {
        match(sym.MODULE);
        String moduleName = consumeId();
        currentModule = new flowchart.Module(moduleName);
        match(sym.SEMICOLON);

        parseDeclarations();

        if (lookahead.type == sym.BEGIN) {
            match(sym.BEGIN);
            // Module-level body isn't a named procedure, but we can create one
            Procedure mainProc = currentModule.add(moduleName);
            currentProcedure = mainProc;
            parseStatementSequence(mainProc);
            currentProcedure = null;
        }

        match(sym.END);
        String endName = consumeId();
        if (!moduleName.equalsIgnoreCase(endName)) {
            System.err.println("Warning: Module name mismatch");
        }
        match(sym.DOT);
    }

    //
    // declarations →
    //   ["CONST" {identifier "=" expression ";"}]
    //   ["TYPE" {identifier "=" type ";"}]
    //   ["VAR" {identifier_list ":" type ";"}]
    //   {procedure_declaration ";"}
    //
    private void parseDeclarations() throws OberonException, IOException {
        // CONST section
        if (lookahead.type == sym.CONST) {
            match(sym.CONST);
            while (lookahead.type == sym.IDENTIFIER) {
                consumeId();
                match(sym.EQ);
                parseExpression();
                match(sym.SEMICOLON);
            }
        }

        // TYPE section
        if (lookahead.type == sym.TYPE) {
            match(sym.TYPE);
            while (lookahead.type == sym.IDENTIFIER) {
                String typeName = consumeId().toLowerCase();
                match(sym.EQ);
                String typeRepr = parseType();
                typeEnvironment.put(typeName, typeRepr);
                match(sym.SEMICOLON);
            }
        }

        // VAR section
        if (lookahead.type == sym.VAR) {
            match(sym.VAR);
            while (lookahead.type == sym.IDENTIFIER) {
                List<String> varNames = parseIdentifierList();
                match(sym.COLON);
                String typeRepr = parseType();
                match(sym.SEMICOLON);
                for (String v : varNames) {
                    typeEnvironment.put(v.toLowerCase(), typeRepr);
                }
            }
        }

        // Procedure declarations
        while (lookahead.type == sym.PROCEDURE) {
            parseProcedureDeclaration();
            if (lookahead.type == sym.SEMICOLON) {
                match(sym.SEMICOLON);
            }
        }
    }

    //
    // procedure_declaration →
    //   "PROCEDURE" identifier [formal_parameters] ";"
    //   procedure_body
    //
    private void parseProcedureDeclaration() throws OberonException, IOException {
        match(sym.PROCEDURE);
        String procName = consumeId();

        // Create new procedure in the flowchart module
        Procedure proc = currentModule.add(procName);
        Procedure savedProc = currentProcedure;
        currentProcedure = proc;

        // Parse formal parameters (for documentation purposes)
        if (lookahead.type == sym.LPAREN) {
            parseFormalParameters();
        }
        match(sym.SEMICOLON);

        // Parse procedure body
        parseProcedureBody(procName);

        currentProcedure = savedProc;
    }

    private void parseFormalParameters() throws OberonException, IOException {
        match(sym.LPAREN);
        if (lookahead.type != sym.RPAREN) {
            // Parse first fp_section
            if (lookahead.type == sym.VAR) {
                match(sym.VAR);
            }
            parseIdentifierList();
            match(sym.COLON);
            parseType();
            // Parse remaining fp_sections
            while (lookahead.type == sym.SEMICOLON) {
                match(sym.SEMICOLON);
                if (lookahead.type == sym.VAR) {
                    match(sym.VAR);
                }
                parseIdentifierList();
                match(sym.COLON);
                parseType();
            }
        }
        match(sym.RPAREN);
    }

    //
    // procedure_body →
    //   declarations
    //   ["BEGIN" statement_sequence]
    //   "END" identifier
    //
    private void parseProcedureBody(String procName) throws OberonException, IOException {
        parseDeclarations();

        if (lookahead.type == sym.BEGIN) {
            match(sym.BEGIN);
            parseStatementSequence(currentProcedure);
        }

        match(sym.END);
        String endName = consumeId();
        if (!procName.equalsIgnoreCase(endName)) {
            System.err.println("Warning: Procedure name mismatch: " + procName +
                             " vs " + endName);
        }
    }

    //
    // statement_sequence → statement {";" statement}
    //
    private void parseStatementSequence(Procedure proc)
            throws OberonException, IOException {
        parseStatement(proc);
        while (lookahead.type == sym.SEMICOLON) {
            match(sym.SEMICOLON);
            parseStatement(proc);
        }
    }

    //
    // statement → assignment | procedure_call | if_statement | while_statement | ε
    //
    /**
     * Helper: add a statement to a container that supports add(AbstractStatement).
     * Used by both parseStatement (→Procedure) and parseStatementToBody (→StatementSequence).
     */
    private static void addTo(Object container, AbstractStatement stmt) {
        if (container instanceof Procedure) {
            ((Procedure) container).add(stmt);
        } else if (container instanceof StatementSequence) {
            ((StatementSequence) container).add(stmt);
        }
    }

    private void parseStatement(Procedure proc) throws OberonException, IOException {
        parseStatementCommon(proc);
    }

    /**
     * Common statement parsing logic shared by parseStatement and parseStatementToBody.
     * Avoids ~120 lines of duplicated code.
     */
    private void parseStatementCommon(Object container) throws OberonException, IOException {
        switch (lookahead.type) {
            case sym.IDENTIFIER: {
                String id = consumeId();
                StringBuilder stmtText = new StringBuilder(id);

                if (lookahead.type == sym.DOT || lookahead.type == sym.LBRACKET) {
                    parseSelectorInStatement(stmtText);
                }

                if (lookahead.type == sym.ASSIGN) {
                    match(sym.ASSIGN);
                    stmtText.append(" := ");
                    stmtText.append(parseExpressionText());
                    addTo(container, new PrimitiveStatement(stmtText.toString()));
                } else if (lookahead.type == sym.LPAREN) {
                    stmtText.append(parseActualParamsText());
                    addTo(container, new PrimitiveStatement(stmtText.toString()));
                } else {
                    addTo(container, new PrimitiveStatement(stmtText.toString()));
                }
                break;
            }
            case sym.IF:
                if (container instanceof Procedure) {
                    parseIfStatement((Procedure) container);
                } else {
                    parseIfStatementToBody((StatementSequence) container);
                }
                break;
            case sym.WHILE:
                if (container instanceof Procedure) {
                    parseWhileStatement((Procedure) container);
                } else {
                    parseWhileStatementToBody((StatementSequence) container);
                }
                break;
            case sym.READ:
            case sym.WRITE:
            case sym.WRITELN: {
                int type = lookahead.type;
                lookahead = scanner.next();
                StringBuilder sb = new StringBuilder();
                if (type == sym.READ) sb.append("Read(");
                else if (type == sym.WRITE) sb.append("Write(");
                else sb.append("WriteLn");

                if (type != sym.WRITELN && lookahead.type == sym.LPAREN) {
                    match(sym.LPAREN);
                    sb.append(parseExpressionText());
                    while (lookahead.type == sym.COMMA) {
                        match(sym.COMMA);
                        sb.append(", ");
                        sb.append(parseExpressionText());
                    }
                    match(sym.RPAREN);
                    sb.append(")");
                }
                addTo(container, new PrimitiveStatement(sb.toString()));
                break;
            }
            default:
                break;
        }
    }

    // ===== Flowchart construction helpers =====

    /**
     * Parse an IF statement and add it to the flowchart.
     */
    private void parseIfStatement(Procedure proc) throws OberonException, IOException {
        match(sym.IF);
        String condition = parseExpressionText();
        match(sym.THEN);

        IfStatement ifStmt = new IfStatement(condition);
        proc.add(ifStmt);

        // Parse TRUE branch
        parseStatementSequenceToBody(ifStmt.getTrueBody());

        // Parse ELSIF branches
        while (lookahead.type == sym.ELSIF) {
            match(sym.ELSIF);
            String elsifCond = parseExpressionText();
            match(sym.THEN);

            // For ELSIF, we nest inside the FALSE branch of the previous IF
            IfStatement nestedIf = new IfStatement(elsifCond);
            ifStmt.getFalseBody().add(nestedIf);
            ifStmt = nestedIf;
            parseStatementSequenceToBody(ifStmt.getTrueBody());
        }

        // Parse ELSE branch
        if (lookahead.type == sym.ELSE) {
            match(sym.ELSE);
            parseStatementSequenceToBody(ifStmt.getFalseBody());
        }

        match(sym.END);
    }

    /**
     * Parse a WHILE statement and add it to the flowchart.
     */
    private void parseWhileStatement(Procedure proc) throws OberonException, IOException {
        match(sym.WHILE);
        String condition = parseExpressionText();
        match(sym.DO);

        WhileStatement whileStmt = new WhileStatement(condition);
        proc.add(whileStmt);

        parseStatementSequenceToBody(whileStmt.getLoopBody());

        match(sym.END);
    }

    /**
     * Parse statement sequence directly into a StatementSequence (for IF/WHILE bodies).
     */
    private void parseStatementSequenceToBody(StatementSequence body)
            throws OberonException, IOException {
        Procedure tempProc = new Procedure("");
        // Temporarily swap the add target
        Procedure savedProc = currentProcedure;
        // We need to add directly to body, so we parse statements and add to body
        parseStatementToBody(body);
        while (lookahead.type == sym.SEMICOLON) {
            match(sym.SEMICOLON);
            parseStatementToBody(body);
        }
        currentProcedure = savedProc;
    }

    private void parseStatementToBody(StatementSequence body)
            throws OberonException, IOException {
        parseStatementCommon(body);
    }

    /** IF statement parsed into a StatementSequence body (for nesting). */
    private void parseIfStatementToBody(StatementSequence body)
            throws OberonException, IOException {
        match(sym.IF);
        String cond = parseExpressionText();
        match(sym.THEN);
        IfStatement ifStmt = new IfStatement(cond);
        body.add(ifStmt);
        parseStatementSequenceToBody(ifStmt.getTrueBody());
        while (lookahead.type == sym.ELSIF) {
            match(sym.ELSIF);
            String econd = parseExpressionText();
            match(sym.THEN);
            IfStatement nested = new IfStatement(econd);
            ifStmt.getFalseBody().add(nested);
            ifStmt = nested;
            parseStatementSequenceToBody(ifStmt.getTrueBody());
        }
        if (lookahead.type == sym.ELSE) {
            match(sym.ELSE);
            parseStatementSequenceToBody(ifStmt.getFalseBody());
        }
        match(sym.END);
    }

    /** WHILE statement parsed into a StatementSequence body (for nesting). */
    private void parseWhileStatementToBody(StatementSequence body)
            throws OberonException, IOException {
        match(sym.WHILE);
        String cond = parseExpressionText();
        match(sym.DO);
        WhileStatement wStmt = new WhileStatement(cond);
        body.add(wStmt);
        parseStatementSequenceToBody(wStmt.getLoopBody());
        match(sym.END);
    }

    // ===== Expression parsing (text reconstruction for flowchart display) =====

    private String parseExpressionText() throws OberonException, IOException {
        StringBuilder sb = new StringBuilder();

        // Optional unary +/-
        if (lookahead.type == sym.PLUS) {
            sb.append("+");
            lookahead = scanner.next();
        } else if (lookahead.type == sym.MINUS) {
            sb.append("-");
            lookahead = scanner.next();
        }

        sb.append(parseTermText());

        while (lookahead.type == sym.PLUS || lookahead.type == sym.MINUS ||
               lookahead.type == sym.OR) {
            if (lookahead.type == sym.PLUS) sb.append(" + ");
            else if (lookahead.type == sym.MINUS) sb.append(" - ");
            else sb.append(" OR ");
            lookahead = scanner.next();
            sb.append(parseTermText());
        }

        // Optional relational operator
        if (lookahead.type == sym.EQ || lookahead.type == sym.NE ||
            lookahead.type == sym.LT || lookahead.type == sym.LE ||
            lookahead.type == sym.GT || lookahead.type == sym.GE) {
            switch (lookahead.type) {
                case sym.EQ: sb.append(" = "); break;
                case sym.NE: sb.append(" # "); break;
                case sym.LT: sb.append(" < "); break;
                case sym.LE: sb.append(" <= "); break;
                case sym.GT: sb.append(" > "); break;
                case sym.GE: sb.append(" >= "); break;
            }
            lookahead = scanner.next();

            // Optional unary in right operand
            if (lookahead.type == sym.PLUS) {
                sb.append("+");
                lookahead = scanner.next();
            } else if (lookahead.type == sym.MINUS) {
                sb.append("-");
                lookahead = scanner.next();
            }
            sb.append(parseTermText());
            while (lookahead.type == sym.PLUS || lookahead.type == sym.MINUS ||
                   lookahead.type == sym.OR) {
                if (lookahead.type == sym.PLUS) sb.append(" + ");
                else if (lookahead.type == sym.MINUS) sb.append(" - ");
                else sb.append(" OR ");
                lookahead = scanner.next();
                sb.append(parseTermText());
            }
        }

        return sb.toString();
    }

    private String parseTermText() throws OberonException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(parseFactorText());
        while (lookahead.type == sym.STAR || lookahead.type == sym.DIV ||
               lookahead.type == sym.MODOP || lookahead.type == sym.AND) {
            switch (lookahead.type) {
                case sym.STAR: sb.append(" * "); break;
                case sym.DIV: sb.append(" DIV "); break;
                case sym.MODOP: sb.append(" MOD "); break;
                case sym.AND: sb.append(" & "); break;
            }
            lookahead = scanner.next();
            sb.append(parseFactorText());
        }
        return sb.toString();
    }

    private String parseFactorText() throws OberonException, IOException {
        switch (lookahead.type) {
            case sym.IDENTIFIER: {
                String id = (String) lookahead.value;
                lookahead = scanner.next();
                StringBuilder sb = new StringBuilder(id);
                // Parse selector
                while (lookahead.type == sym.DOT || lookahead.type == sym.LBRACKET) {
                    if (lookahead.type == sym.DOT) {
                        match(sym.DOT);
                        sb.append(".").append(consumeId());
                    } else {
                        match(sym.LBRACKET);
                        sb.append("[").append(parseExpressionText()).append("]");
                        match(sym.RBRACKET);
                    }
                }
                return sb.toString();
            }
            case sym.INTEGER_LITERAL: {
                int val = (Integer) lookahead.value;
                lookahead = scanner.next();
                return String.valueOf(val);
            }
            case sym.LPAREN:
                match(sym.LPAREN);
                String expr = parseExpressionText();
                match(sym.RPAREN);
                return "(" + expr + ")";
            case sym.NOT:
                match(sym.NOT);
                return "~" + parseFactorText();
            default:
                throw new MissingOperandException(
                    "Expected factor but got " + sym.nameOf(lookahead.type) +
                    " at line " + lookahead.line + ", column " + lookahead.column);
        }
    }

    private String parseActualParamsText() throws OberonException, IOException {
        StringBuilder sb = new StringBuilder("(");
        match(sym.LPAREN);
        if (lookahead.type != sym.RPAREN) {
            sb.append(parseExpressionText());
            while (lookahead.type == sym.COMMA) {
                match(sym.COMMA);
                sb.append(", ");
                sb.append(parseExpressionText());
            }
        }
        match(sym.RPAREN);
        sb.append(")");
        return sb.toString();
    }

    private void parseExpression() throws OberonException, IOException {
        // Simplified expression parsing (no text reconstruction needed for non-flowchart mode)
        parseExpressionText();
    }

    // ===== Type and Identifier list parsers =====

    private String parseType() throws OberonException, IOException {
        switch (lookahead.type) {
            case sym.IDENTIFIER:
                return consumeId();
            case sym.ARRAY: {
                match(sym.ARRAY);
                parseExpression();
                match(sym.OF);
                String elemType = parseType();
                return "ARRAY OF " + elemType;
            }
            case sym.RECORD: {
                match(sym.RECORD);
                StringBuilder sb = new StringBuilder("RECORD");
                if (lookahead.type == sym.IDENTIFIER) {
                    parseIdentifierList();
                    match(sym.COLON);
                    sb.append(" ").append(parseType());
                }
                while (lookahead.type == sym.SEMICOLON) {
                    match(sym.SEMICOLON);
                    if (lookahead.type == sym.IDENTIFIER) {
                        parseIdentifierList();
                        match(sym.COLON);
                        sb.append("; ").append(parseType());
                    }
                }
                match(sym.END);
                return sb.append(" END").toString();
            }
            default:
                error("Expected type");
                return null;
        }
    }

    private List<String> parseIdentifierList() throws OberonException, IOException {
        List<String> ids = new ArrayList<String>();
        ids.add(consumeId());
        while (lookahead.type == sym.COMMA) {
            match(sym.COMMA);
            ids.add(consumeId());
        }
        return ids;
    }

    private void parseSelector() throws OberonException, IOException {
        while (lookahead.type == sym.DOT || lookahead.type == sym.LBRACKET) {
            if (lookahead.type == sym.DOT) {
                match(sym.DOT);
                consumeId();
            } else {
                match(sym.LBRACKET);
                parseExpression();
                match(sym.RBRACKET);
            }
        }
    }

    private void parseSelectorInStatement(StringBuilder sb) throws OberonException, IOException {
        while (lookahead.type == sym.DOT || lookahead.type == sym.LBRACKET) {
            if (lookahead.type == sym.DOT) {
                match(sym.DOT);
                sb.append(".").append(consumeId());
            } else {
                match(sym.LBRACKET);
                sb.append("[");
                // Save current lookahead to reconstruct text
                sb.append(parseExpressionText());
                sb.append("]");
                match(sym.RBRACKET);
            }
        }
    }

    // ===== Main entry point =====

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java OberonParser <source-file>");
            System.exit(1);
        }

        try {
            OberonScanner scanner = new OberonScanner(new FileReader(args[0]));
            OberonParser parser = new OberonParser(scanner);
            parser.parse();
        } catch (LexicalException e) {
            System.err.println("LEXICAL ERROR: " + e.getMessage());
            System.exit(1);
        } catch (SyntacticException e) {
            System.err.println("SYNTAX ERROR: " + e.getMessage());
            System.exit(1);
        } catch (SemanticException e) {
            System.err.println("SEMANTIC ERROR: " + e.getMessage());
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

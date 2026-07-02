/**
 * Oberon-0 LALR-style syntax-directed translation parser.
 *
 * <p>This parser is designed to work with OberonScanner to parse Oberon-0
 * source programs and generate a Call Graph using the ROSE CallGraph API.
 *
 * <p>It implements syntax-directed translation with embedded semantic actions
 * that build the call graph during parsing, similar to what JavaCUP generates.
 *
 * <p>Key functions:
 * <ul>
 *   <li>Parse Oberon-0 programs according to the EBNF grammar</li>
 *   <li>Detect and classify lexical, syntax, and semantic errors</li>
 *   <li>Generate a call graph for correct programs</li>
 * </ul>
 *
 * @author Lianglihang
 * @version 1.00
 */
import callgraph.*;
import exceptions.*;
import oberon.OberonScanner;
import oberon.OberonScanner.Token;
import java.io.*;
import java.util.*;

public class Parser {
    private final OberonScanner scanner;
    private OberonScanner.Token currentToken;

    // --- Call graph construction state ---
    private CallGraph callGraph;
    private String currentProcedure;
    private int callSiteCounter;

    // --- Semantic analysis state ---
    private Set<String> declaredProcedures;
    private Map<String, List<String>> procedureParams;
    private Map<String, String> declaredTypes;
    private Map<String, String> declaredVars;

    // --- Lookahead ---
    private OberonScanner.Token lookahead;

    public Parser(OberonScanner scanner) throws IOException, OberonException {
        this.scanner = scanner;
        this.callGraph = new CallGraph();
        this.currentProcedure = null;
        this.callSiteCounter = 0;
        this.declaredProcedures = new HashSet<String>();
        this.procedureParams = new HashMap<String, List<String>>();
        this.declaredTypes = new HashMap<String, String>();
        this.declaredVars = new HashMap<String, String>();
        this.lookahead = scanner.next();
    }

    /**
     * Parse an Oberon-0 module and generate the call graph.
     *
     * @throws OberonException if any lexical, syntactic, or semantic error is found
     * @throws IOException if an I/O error occurs
     */
    public void parse() throws OberonException, IOException {
        parseModule();
        callGraph.show();
    }

    /**
     * Returns the constructed call graph.
     */
    public CallGraph getCallGraph() {
        return callGraph;
    }

    // ===== Utility Methods =====

    private void match(int expectedType) throws OberonException, IOException {
        if (lookahead.type == expectedType) {
            lookahead = scanner.next();
        } else {
            error("Expected " + sym.nameOf(expectedType) + " but got " +
                  sym.nameOf(lookahead.type));
        }
    }

    private void match(int expectedType, String context) throws OberonException, IOException {
        if (lookahead.type == expectedType) {
            lookahead = scanner.next();
        } else {
            error("Expected " + sym.nameOf(expectedType) +
                  " in " + context + " but got " + sym.nameOf(lookahead.type));
        }
    }

    private String consumeIdentifier() throws OberonException, IOException {
        if (lookahead.type == sym.IDENTIFIER) {
            String id = (String) lookahead.value;
            lookahead = scanner.next();
            return id;
        } else {
            error("Expected identifier but got " + sym.nameOf(lookahead.type));
            return null;
        }
    }

    private void error(String message) throws OberonException {
        int line = lookahead.line;
        int col = lookahead.column;
        // Determine error category
        if (lookahead.type == sym.EOF) {
            throw new MissingOperandException(
                "Unexpected end of file: " + message + " at line " + line + ", column " + col);
        }
        throw new SyntacticException(
            message + " at line " + line + ", column " + col);
    }

    private void semanticError(String message) throws SemanticException {
        int line = lookahead.line;
        int col = lookahead.column;
        throw new SemanticException(message + " at line " + line + ", column " + col);
    }

    // ===== Grammar Rules =====
    //
    // module = "MODULE" identifier ";"
    //          declarations
    //          ["BEGIN" statement_sequence]
    //          "END" identifier "." ;

    private void parseModule() throws OberonException, IOException {
        match(sym.MODULE, "module declaration");
        String moduleName = consumeIdentifier();
        match(sym.SEMICOLON, "module declaration");

        parseDeclarations();

        if (lookahead.type == sym.BEGIN) {
            match(sym.BEGIN);
            parseStatementSequence();
        }

        match(sym.END, "module body");
        String endName = consumeIdentifier();
        if (!moduleName.equalsIgnoreCase(endName)) {
            semanticError("Module name mismatch: '" + moduleName +
                         "' at start vs '" + endName + "' at END");
        }
        match(sym.DOT, "module end");
    }

    // declarations = ["CONST" {identifier "=" expression ";"}]
    //                ["TYPE" {identifier "=" type ";"}]
    //                ["VAR" {identifier_list ":" type ";"}]
    //                {procedure_declaration ";"} ;

    private void parseDeclarations() throws OberonException, IOException {
        // CONST declarations
        if (lookahead.type == sym.CONST) {
            match(sym.CONST);
            while (lookahead.type == sym.IDENTIFIER) {
                String constName = consumeIdentifier();
                match(sym.EQ, "constant declaration");
                parseExpression();
                match(sym.SEMICOLON, "constant declaration");
            }
        }

        // TYPE declarations
        if (lookahead.type == sym.TYPE) {
            match(sym.TYPE);
            while (lookahead.type == sym.IDENTIFIER) {
                String typeName = consumeIdentifier();
                match(sym.EQ, "type declaration");
                String typeRepr = parseType();
                declaredTypes.put(typeName.toLowerCase(), typeRepr);
                match(sym.SEMICOLON, "type declaration");
            }
        }

        // VAR declarations
        if (lookahead.type == sym.VAR) {
            match(sym.VAR);
            while (lookahead.type == sym.IDENTIFIER) {
                List<String> vars = parseIdentifierList();
                match(sym.COLON, "variable declaration");
                String typeRepr = parseType();
                match(sym.SEMICOLON, "variable declaration");
                for (String varName : vars) {
                    declaredVars.put(varName.toLowerCase(), typeRepr);
                }
            }
        }

        // Procedure declarations (may be mixed with VAR)
        while (lookahead.type == sym.PROCEDURE) {
            parseProcedureDeclaration();
            if (lookahead.type == sym.SEMICOLON) {
                match(sym.SEMICOLON);
            }
        }
    }

    // procedure_declaration = procedure_heading ";" procedure_body ;

    private void parseProcedureDeclaration() throws OberonException, IOException {
        String procName = parseProcedureHeading();
        String savedProcedure = currentProcedure;
        currentProcedure = procName;

        // Save state for the procedure body
        Map<String, String> savedVars = new HashMap<String, String>(declaredVars);
        Map<String, String> savedTypes = new HashMap<String, String>(declaredTypes);

        parseProcedureBody(procName);

        // Restore outer scope
        declaredVars = savedVars;
        declaredTypes = savedTypes;
        currentProcedure = savedProcedure;
    }

    // procedure_heading = "PROCEDURE" identifier [formal_parameters] ;

    private String parseProcedureHeading() throws OberonException, IOException {
        match(sym.PROCEDURE, "procedure declaration");
        String procName = consumeIdentifier();

        // Register the procedure in the call graph
        callGraph.addProcedure(procName, procName);
        declaredProcedures.add(procName.toLowerCase());

        // Parse formal parameters
        List<String> params = new ArrayList<String>();
        if (lookahead.type == sym.LPAREN) {
            params = parseFormalParameters();
        }
        procedureParams.put(procName.toLowerCase(), params);

        match(sym.SEMICOLON, "procedure heading");
        return procName;
    }

    // formal_parameters = "(" [fp_section {";" fp_section}] ")" ;

    private List<String> parseFormalParameters() throws OberonException, IOException {
        List<String> params = new ArrayList<String>();
        match(sym.LPAREN, "formal parameters");

        if (lookahead.type != sym.RPAREN) {
            // Parse fp_sections
            params.addAll(parseFpSection());
            while (lookahead.type == sym.SEMICOLON) {
                match(sym.SEMICOLON);
                params.addAll(parseFpSection());
            }
        }

        match(sym.RPAREN, "formal parameters");
        return params;
    }

    private List<String> parseFpSection() throws OberonException, IOException {
        boolean isVar = false;
        if (lookahead.type == sym.VAR) {
            match(sym.VAR);
            isVar = true;
        }
        List<String> ids = parseIdentifierList();
        match(sym.COLON, "formal parameter section");
        String typeRepr = parseType();

        List<String> result = new ArrayList<String>();
        for (String id : ids) {
            result.add((isVar ? "VAR " : "") + id + ": " + typeRepr);
        }
        return result;
    }

    // procedure_body = declarations ["BEGIN" statement_sequence] "END" identifier ;

    private void parseProcedureBody(String procName) throws OberonException, IOException {
        parseDeclarations();

        if (lookahead.type == sym.BEGIN) {
            match(sym.BEGIN);
            parseStatementSequence();
        }

        match(sym.END, "procedure body");
        String endName = consumeIdentifier();
        if (!procName.equalsIgnoreCase(endName)) {
            semanticError("Procedure name mismatch: '" + procName +
                         "' at start vs '" + endName + "' at END");
        }
    }

    // statement_sequence = statement {";" statement} ;

    private void parseStatementSequence() throws OberonException, IOException {
        parseStatement();
        while (lookahead.type == sym.SEMICOLON) {
            match(sym.SEMICOLON);
            parseStatement();
        }
    }

    // statement = [assignment | procedure_call | if_statement | while_statement] ;

    private void parseStatement() throws OberonException, IOException {
        switch (lookahead.type) {
            case sym.IDENTIFIER:
                // Could be assignment or procedure call - need lookahead
                parseAssignmentOrCall();
                break;
            case sym.IF:
                parseIfStatement();
                break;
            case sym.WHILE:
                parseWhileStatement();
                break;
            case sym.READ:
            case sym.WRITE:
            case sym.WRITELN:
                parsePredefinedCall();
                break;
            // Empty statement or unexpected - skip
            default:
                break;
        }
    }

    private void parseAssignmentOrCall() throws OberonException, IOException {
        String id = consumeIdentifier();

        // Check if it's a procedure call (followed by actual params or on its own)
        // or an assignment (followed by selector and :=)
        if (lookahead.type == sym.LPAREN) {
            // Definitely a procedure call
            addCallEdge(id);
            parseActualParameters();
        } else if (lookahead.type == sym.DOT || lookahead.type == sym.LBRACKET) {
            // Selector - could be part of assignment or procedure
            parseSelector();
            if (lookahead.type == sym.ASSIGN) {
                match(sym.ASSIGN);
                parseExpression();
            } else if (lookahead.type == sym.LPAREN) {
                addCallEdge(id);
                parseActualParameters();
            }
        } else if (lookahead.type == sym.ASSIGN) {
            // Assignment
            match(sym.ASSIGN);
            parseExpression();
        } else {
            // Could be a parameterless procedure call
            addCallEdge(id);
        }
    }

    private void parsePredefinedCall() throws OberonException, IOException {
        // READ, WRITE, WRITELN
        int tokenType = lookahead.type;
        lookahead = scanner.next();

        if (tokenType == sym.WRITELN) {
            // WRITELN has no parentheses
            return;
        }

        // READ and WRITE have actual parameters
        if (lookahead.type == sym.LPAREN) {
            parseActualParameters();
        }
    }

    private void addCallEdge(String procName) {
        if (currentProcedure != null && declaredProcedures.contains(procName.toLowerCase())) {
            String callSiteId = "cs" + (++callSiteCounter);
            try {
                callGraph.addCallSite(callSiteId, currentProcedure,
                                     procName.toLowerCase() + "(...)");
                callGraph.addEdge(callSiteId, procName.toLowerCase());
            } catch (Exception e) {
                System.err.println("Warning: Failed to add call edge: " + e.getMessage());
            }
        }
    }

    private void parseActualParameters() throws OberonException, IOException {
        match(sym.LPAREN, "actual parameters");
        if (lookahead.type != sym.RPAREN) {
            parseExpression();
            while (lookahead.type == sym.COMMA) {
                match(sym.COMMA);
                parseExpression();
            }
        }
        match(sym.RPAREN, "actual parameters");
    }

    // while_statement = "WHILE" expression "DO" statement_sequence "END" ;

    private void parseWhileStatement() throws OberonException, IOException {
        match(sym.WHILE);
        parseExpression();
        match(sym.DO, "WHILE statement");
        parseStatementSequence();
        match(sym.END, "WHILE statement");
    }

    // if_statement = "IF" expression "THEN" statement_sequence
    //                {"ELSIF" expression "THEN" statement_sequence}
    //                ["ELSE" statement_sequence] "END" ;

    private void parseIfStatement() throws OberonException, IOException {
        match(sym.IF);
        parseExpression();
        match(sym.THEN, "IF statement");
        parseStatementSequence();

        while (lookahead.type == sym.ELSIF) {
            match(sym.ELSIF);
            parseExpression();
            match(sym.THEN, "ELSIF clause");
            parseStatementSequence();
        }

        if (lookahead.type == sym.ELSE) {
            match(sym.ELSE);
            parseStatementSequence();
        }

        match(sym.END, "IF statement");
    }

    // type = identifier | array_type | record_type ;

    private String parseType() throws OberonException, IOException {
        switch (lookahead.type) {
            case sym.IDENTIFIER:
                return consumeIdentifier();
            case sym.ARRAY:
                return parseArrayType();
            case sym.RECORD:
                return parseRecordType();
            default:
                throw new SyntacticException("Expected type but got " +
                    sym.nameOf(lookahead.type) + " at line " +
                    lookahead.line + ", column " + lookahead.column);
        }
    }

    private String parseArrayType() throws OberonException, IOException {
        match(sym.ARRAY);
        parseExpression();
        match(sym.OF, "array type");
        String elemType = parseType();
        return "ARRAY OF " + elemType;
    }

    private String parseRecordType() throws OberonException, IOException {
        StringBuilder sb = new StringBuilder("RECORD ");
        match(sym.RECORD);
        if (lookahead.type == sym.IDENTIFIER) {
            // field_list
            parseIdentifierList();
            match(sym.COLON, "record field");
            sb.append(parseType());
        }
        while (lookahead.type == sym.SEMICOLON) {
            match(sym.SEMICOLON);
            if (lookahead.type == sym.IDENTIFIER) {
                parseIdentifierList();
                match(sym.COLON, "record field");
                sb.append("; ").append(parseType());
            }
        }
        match(sym.END, "record type");
        sb.append(" END");
        return sb.toString();
    }

    // identifier_list = identifier {"," identifier} ;

    private List<String> parseIdentifierList() throws OberonException, IOException {
        List<String> ids = new ArrayList<String>();
        ids.add(consumeIdentifier());
        while (lookahead.type == sym.COMMA) {
            match(sym.COMMA);
            ids.add(consumeIdentifier());
        }
        return ids;
    }

    // expression = simple_expression [("="|"#"|"<"|"<="|">"|">=") simple_expression] ;

    private void parseExpression() throws OberonException, IOException {
        parseSimpleExpression();
        switch (lookahead.type) {
            case sym.EQ: case sym.NE:
            case sym.LT: case sym.LE:
            case sym.GT: case sym.GE:
                lookahead = scanner.next();
                parseSimpleExpression();
                break;
        }
    }

    // simple_expression = ["+" | "-"] term {("+" | "-" | "OR") term} ;

    private void parseSimpleExpression() throws OberonException, IOException {
        if (lookahead.type == sym.PLUS || lookahead.type == sym.MINUS) {
            lookahead = scanner.next();
        }
        parseTerm();
        while (lookahead.type == sym.PLUS || lookahead.type == sym.MINUS ||
               lookahead.type == sym.OR) {
            lookahead = scanner.next();
            parseTerm();
        }
    }

    // term = factor {("*" | "DIV" | "MOD" | "&") factor} ;

    private void parseTerm() throws OberonException, IOException {
        parseFactor();
        while (lookahead.type == sym.STAR || lookahead.type == sym.DIV ||
               lookahead.type == sym.MODOP || lookahead.type == sym.AND) {
            lookahead = scanner.next();
            parseFactor();
        }
    }

    // factor = identifier selector | number | "(" expression ")" | "~" factor ;

    private void parseFactor() throws OberonException, IOException {
        switch (lookahead.type) {
            case sym.IDENTIFIER:
                consumeIdentifier();
                parseSelector();
                break;
            case sym.INTEGER_LITERAL:
                lookahead = scanner.next();
                break;
            case sym.LPAREN:
                match(sym.LPAREN);
                parseExpression();
                match(sym.RPAREN, "factor");
                break;
            case sym.NOT:
                match(sym.NOT);
                parseFactor();
                break;
            default:
                throw new MissingOperandException(
                    "Expected factor (identifier, number, '(' or '~') but got " +
                    sym.nameOf(lookahead.type) + " at line " +
                    lookahead.line + ", column " + lookahead.column);
        }
    }

    // selector = {"." identifier | "[" expression "]"} ;

    private void parseSelector() throws OberonException, IOException {
        while (lookahead.type == sym.DOT || lookahead.type == sym.LBRACKET) {
            if (lookahead.type == sym.DOT) {
                match(sym.DOT);
                consumeIdentifier();
            } else {
                match(sym.LBRACKET);
                parseExpression();
                match(sym.RBRACKET, "array selector");
            }
        }
    }

    // ===== Main entry point =====

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Parser <source-file>");
            System.exit(1);
        }

        try {
            OberonScanner scanner = new OberonScanner(new FileReader(args[0]));
            Parser parser = new Parser(scanner);
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

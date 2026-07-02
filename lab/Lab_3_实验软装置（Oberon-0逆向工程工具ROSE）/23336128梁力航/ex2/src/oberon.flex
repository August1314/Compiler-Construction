/**
 * JFlex input file for Oberon-0 language lexical scanner.
 *
 * Generates OberonScanner.java.
 *
 * Usage: jflex oberon.flex
 *
 * @author Lianglihang
 * @version 1.00
 */

import exceptions.*;

%%

%class OberonScanner
%unicode
%line
%column
%type java_cup.runtime.Symbol
%cup
%public

%{
  private StringBuilder stringBuilder = new StringBuilder();

  /**
   * Creates and returns a Symbol for use by JavaCUP parser.
   */
  private Symbol symbol(int type) {
    return new Symbol(type, yyline + 1, yycolumn + 1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline + 1, yycolumn + 1, value);
  }

  /**
   * Reports an error by throwing the given exception.
   */
  private void reportError(String message, LexicalException exception)
      throws LexicalException {
    throw exception;
  }

  /**
   * Checks if an identifier exceeds the 24-character length limit.
   */
  private String checkIdentifierLength(String id) throws IllegalIdentifierLengthException {
    if (id.length() > 24) {
      throw new IllegalIdentifierLengthException(
        "Identifier \"" + id + "\" exceeds maximum length of 24 characters " +
        "at line " + (yyline + 1) + ", column " + (yycolumn + 1));
    }
    return id;
  }
%}

/* --- Line terminators --- */
LineTerminator = \r|\n|\r\n

/* --- White space --- */
WhiteSpace = {LineTerminator} | [ \t\f]

/* --- Integer constants --- */
Digit = [0-9]
OctalDigit = [0-7]

/* Decimal integer: first digit non-zero, up to 12 digits total */
DecimalInteger = [1-9]{Digit}{0,11}
/* Octal integer: starts with 0, followed by up to 11 octal digits */
OctalInteger = 0{OctalDigit}{0,11}

Number = {DecimalInteger} | {OctalInteger}

/* --- Identifiers --- */
Letter = [a-zA-Z]
Identifier = {Letter}({Letter}|{Digit})*

/* --- Comments --- */
CommentContent = [^*] | "*"+[^*)]

%state STRING

%%

/* --- White space --- */
{WhiteSpace}  { /* ignore */ }

/* --- Comments --- */
"(*" {CommentContent}* "*"+ ")"  { /* ignore comment */ }
"(*" {CommentContent}* {
  throw new MismatchedCommentException(
    "Unclosed comment starting at line " + (yyline + 1) + ", column " + (yycolumn + 1));
}

/* --- Reserved words (case-insensitive) --- */
<YYINITIAL> {
  "MODULE"    { return symbol(sym.MODULE); }
  "BEGIN"     { return symbol(sym.BEGIN); }
  "END"       { return symbol(sym.END); }
  "CONST"     { return symbol(sym.CONST); }
  "TYPE"      { return symbol(sym.TYPE); }
  "VAR"       { return symbol(sym.VAR); }
  "PROCEDURE" { return symbol(sym.PROCEDURE); }
  "IF"        { return symbol(sym.IF); }
  "THEN"      { return symbol(sym.THEN); }
  "ELSIF"     { return symbol(sym.ELSIF); }
  "ELSE"      { return symbol(sym.ELSE); }
  "WHILE"     { return symbol(sym.WHILE); }
  "DO"        { return symbol(sym.DO); }
  "ARRAY"     { return symbol(sym.ARRAY); }
  "OF"        { return symbol(sym.OF); }
  "RECORD"    { return symbol(sym.RECORD); }
  "DIV"       { return symbol(sym.DIV); }
  "MOD"       { return symbol(sym.MODOP); }
  "OR"        { return symbol(sym.OR); }

  /* --- Predefined type keywords (INTEGER, BOOLEAN, TRUE, FALSE
       are keywords/predefined identifiers in Oberon-0, NOT reserved words.
       The scanner returns them as IDENTIFIER tokens; the parser handles
       them contextually. This matches the hand-written OberonScanner.java. --- */

  /* --- Predefined procedure names (keywords) --- */
  "READ"      { return symbol(sym.READ); }
  "WRITE"     { return symbol(sym.WRITE); }
  "WRITELN"   { return symbol(sym.WRITELN); }

  /* --- Integer constants --- */
  {DecimalInteger} {
    String text = yytext();
    // Check for illegal concatenation with identifier: peek ahead
    int len = text.length();
    if (len > 12) {
      throw new IllegalIntegerRangeException(
        "Integer constant \"" + text + "\" exceeds maximum 12 digits " +
        "at line " + (yyline + 1) + ", column " + (yycolumn + 1));
    }
    // Check if next character is a letter (no space after integer before identifier)
    if (yylength() < zzEndRead - zzStartRead) {
      // Use lookahead to check for illegal concatenation
      int nextChar = zzBuffer[zzCurrentPos];
      if (nextChar != -1 && Character.isLetter((char) nextChar)) {
        throw new IllegalIntegerException(
          "Missing separator between integer \"" + text + "\" and following identifier " +
          "at line " + (yyline + 1) + ", column " + (yycolumn + 1));
      }
    }
    return symbol(sym.INTEGER_LITERAL, Integer.parseInt(text));
  }

  {OctalInteger} {
    String text = yytext();
    int len = text.length();
    if (len > 12) {
      throw new IllegalIntegerRangeException(
        "Octal constant \"" + text + "\" exceeds maximum 12 digits " +
        "at line " + (yyline + 1) + ", column " + (yycolumn + 1));
    }
    // Check for illegal octal digits (8, 9)
    for (int i = 1; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '8' || c == '9') {
        throw new IllegalOctalException(
          "Illegal octal digit '" + c + "' in constant \"" + text + "\" " +
          "at line " + (yyline + 1) + ", column " + (yycolumn + 1));
      }
    }
    int value = Integer.parseInt(text, 8);
    return symbol(sym.INTEGER_LITERAL, value);
  }

  /* --- Identifiers --- */
  {Identifier} {
    String id = checkIdentifierLength(yytext());
    return symbol(sym.IDENTIFIER, id.toLowerCase());
  }

  /* --- Operators and delimiters --- */
  ":="  { return symbol(sym.ASSIGN); }
  "="   { return symbol(sym.EQ); }
  "#"   { return symbol(sym.NE); }
  "<"   { return symbol(sym.LT); }
  "<="  { return symbol(sym.LE); }
  ">"   { return symbol(sym.GT); }
  ">="  { return symbol(sym.GE); }
  "+"   { return symbol(sym.PLUS); }
  "-"   { return symbol(sym.MINUS); }
  "*"   { return symbol(sym.STAR); }
  "&"   { return symbol(sym.AND); }
  "~"   { return symbol(sym.NOT); }
  ";"   { return symbol(sym.SEMICOLON); }
  "."   { return symbol(sym.DOT); }
  ","   { return symbol(sym.COMMA); }
  "("   { return symbol(sym.LPAREN); }
  ")"   { return symbol(sym.RPAREN); }
  "["   { return symbol(sym.LBRACKET); }
  "]"   { return symbol(sym.RBRACKET); }
  ":"   { return symbol(sym.COLON); }
}

/* --- Error fallback: illegal symbol --- */
[^] {
  String ch = yytext();
  throw new IllegalSymbolException(
    "Illegal symbol '" + ch + "' " +
    "at line " + (yyline + 1) + ", column " + (yycolumn + 1));
}

<<EOF>> {
  return symbol(sym.EOF);
}

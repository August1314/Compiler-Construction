package parser;

import java.util.Locale;

import exceptions.ExpressionException;
import exceptions.IllegalDecimalException;
import exceptions.IllegalIdentifierException;
import exceptions.IllegalSymbolException;

/**
 * Lexical scanner that follows the PDF specification strictly.
 */
final class Scanner
{
	private final String input;
	private final int length;
	private int index;
	private Token cached;

	Scanner(String input)
	{
		this.input = input;
		this.length = input.length();
		this.index = 0;
		this.cached = null;
	}

	Token peek() throws ExpressionException
	{
		if (cached == null)
		{
			cached = readToken();
		}
		return cached;
	}

	Token next() throws ExpressionException
	{
		Token token = peek();
		cached = null;
		return token;
	}

	private Token readToken() throws ExpressionException
	{
		skipSpaces();
		if (index >= length)
		{
			return Token.end();
		}

		char ch = input.charAt(index);
		if (Character.isWhitespace(ch))
		{
			throw new IllegalSymbolException("Only space is a valid separator.");
		}
		if (ch == '.')
		{
			throw new IllegalDecimalException("Illegal decimal literal.");
		}
		if (Character.isDigit(ch))
		{
			return readNumber();
		}
		if (Character.isLetter(ch))
		{
			return readIdentifier();
		}

		switch (ch)
		{
		case '(':
			index++;
			return Token.simple(TokenType.LPAREN, "(");
		case ')':
			index++;
			return Token.simple(TokenType.RPAREN, ")");
		case ',':
			index++;
			return Token.simple(TokenType.COMMA, ",");
		case '?':
			index++;
			return Token.simple(TokenType.QUESTION, "?");
		case ':':
			index++;
			return Token.simple(TokenType.COLON, ":");
		case '+':
			index++;
			return Token.simple(TokenType.PLUS, "+");
		case '-':
			index++;
			return Token.simple(TokenType.MINUS, "-");
		case '*':
			index++;
			return Token.simple(TokenType.STAR, "*");
		case '/':
			index++;
			return Token.simple(TokenType.SLASH, "/");
		case '^':
			index++;
			return Token.simple(TokenType.CARET, "^");
		case '!':
			index++;
			return Token.simple(TokenType.NOT, "!");
		case '&':
			index++;
			return Token.simple(TokenType.AND, "&");
		case '|':
			index++;
			return Token.simple(TokenType.OR, "|");
		case '=':
			index++;
			return Token.simple(TokenType.EQ, "=");
		case '<':
			if (startsWith("<="))
			{
				index += 2;
				return Token.simple(TokenType.LE, "<=");
			}
			if (startsWith("<>"))
			{
				index += 2;
				return Token.simple(TokenType.NE, "<>");
			}
			index++;
			return Token.simple(TokenType.LT, "<");
		case '>':
			if (startsWith(">="))
			{
				index += 2;
				return Token.simple(TokenType.GE, ">=");
			}
			index++;
			return Token.simple(TokenType.GT, ">");
		default:
			throw new IllegalSymbolException("Illegal symbol: " + ch);
		}
	}

	private void skipSpaces()
	{
		while (index < length && input.charAt(index) == ' ')
		{
			index++;
		}
	}

	private boolean startsWith(String text)
	{
		return input.startsWith(text, index);
	}

	private Token readIdentifier() throws ExpressionException
	{
		int start = index;
		while (index < length && Character.isLetter(input.charAt(index)))
		{
			index++;
		}
		String letters = input.substring(start, index);
		String normalized = letters.toLowerCase(Locale.ROOT);

		if ("true".equals(normalized))
		{
			return Token.bool(letters, true);
		}
		if ("false".equals(normalized))
		{
			return Token.bool(letters, false);
		}
		if (isFunctionName(normalized))
		{
			return Token.identifier(normalized);
		}

		while (index < length && Character.isLetterOrDigit(input.charAt(index)))
		{
			index++;
		}
		throw new IllegalIdentifierException("Illegal identifier: " + input.substring(start, index));
	}

	private boolean isFunctionName(String identifier)
	{
		return "sin".equals(identifier) || "cos".equals(identifier)
			|| "max".equals(identifier) || "min".equals(identifier);
	}

	private Token readNumber() throws IllegalDecimalException
	{
		int start = index;
		readDigits();

		if (index < length && input.charAt(index) == '.')
		{
			index++;
			if (index >= length || !Character.isDigit(input.charAt(index)))
			{
				throw new IllegalDecimalException("Illegal decimal literal: " + input.substring(start, index));
			}
			readDigits();
		}

		if (index < length)
		{
			char ch = input.charAt(index);
			if (ch == 'e' || ch == 'E')
			{
				index++;
				if (index < length && (input.charAt(index) == '+' || input.charAt(index) == '-'))
				{
					index++;
				}
				if (index >= length || !Character.isDigit(input.charAt(index)))
				{
					throw new IllegalDecimalException("Illegal decimal literal: " + input.substring(start, index));
				}
				readDigits();
			}
		}

		String lexeme = input.substring(start, index);
		return Token.number(lexeme, Double.parseDouble(lexeme));
	}

	private void readDigits()
	{
		while (index < length && Character.isDigit(input.charAt(index)))
		{
			index++;
		}
	}
}

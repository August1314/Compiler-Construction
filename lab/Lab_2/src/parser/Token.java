package parser;

/**
 * Immutable lexical token.
 */
final class Token
{
	private final TokenType type;
	private final String text;
	private final double numberValue;
	private final boolean booleanValue;

	private Token(TokenType type, String text, double numberValue, boolean booleanValue)
	{
		this.type = type;
		this.text = text;
		this.numberValue = numberValue;
		this.booleanValue = booleanValue;
	}

	static Token end()
	{
		return new Token(TokenType.END, "", 0.0, false);
	}

	static Token number(String text, double value)
	{
		return new Token(TokenType.NUMBER, text, value, false);
	}

	static Token bool(String text, boolean value)
	{
		return new Token(TokenType.BOOLEAN, text, 0.0, value);
	}

	static Token identifier(String text)
	{
		return new Token(TokenType.IDENTIFIER, text, 0.0, false);
	}

	static Token simple(TokenType type, String text)
	{
		return new Token(type, text, 0.0, false);
	}

	TokenType getType()
	{
		return type;
	}

	String getText()
	{
		return text;
	}

	double getNumberValue()
	{
		return numberValue;
	}

	boolean getBooleanValue()
	{
		return booleanValue;
	}
}

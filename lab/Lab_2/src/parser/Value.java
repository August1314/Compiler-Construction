package parser;

import exceptions.TypeMismatchedException;

/**
 * Runtime value reduced by the parser.
 */
final class Value
{
	private final boolean booleanType;
	private final double numberValue;
	private final boolean booleanValue;

	private Value(double numberValue)
	{
		this.booleanType = false;
		this.numberValue = numberValue;
		this.booleanValue = false;
	}

	private Value(boolean booleanValue)
	{
		this.booleanType = true;
		this.numberValue = 0.0;
		this.booleanValue = booleanValue;
	}

	static Value ofNumber(double numberValue)
	{
		return new Value(numberValue);
	}

	static Value ofBoolean(boolean booleanValue)
	{
		return new Value(booleanValue);
	}

	boolean isBoolean()
	{
		return booleanType;
	}

	double asNumber() throws TypeMismatchedException
	{
		if (booleanType)
		{
			throw new TypeMismatchedException("Numeric value expected.");
		}
		return numberValue;
	}

	boolean asBoolean() throws TypeMismatchedException
	{
		if (!booleanType)
		{
			throw new TypeMismatchedException("Boolean value expected.");
		}
		return booleanValue;
	}
}

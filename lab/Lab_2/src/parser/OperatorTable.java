package parser;

/**
 * Operator-precedence table used by the shift/reduce parser.
 */
final class OperatorTable
{
	private OperatorTable()
	{
	}

	enum Associativity
	{
		LEFT,
		RIGHT
	}

	enum Operator
	{
		SENTINEL(0, Associativity.LEFT),
		LEFT_PAREN(0, Associativity.LEFT),
		QUESTION(1, Associativity.RIGHT),
		TERNARY(1, Associativity.RIGHT),
		OR(2, Associativity.LEFT),
		AND(3, Associativity.LEFT),
		LT(4, Associativity.LEFT),
		LE(4, Associativity.LEFT),
		GT(4, Associativity.LEFT),
		GE(4, Associativity.LEFT),
		EQ(4, Associativity.LEFT),
		NE(4, Associativity.LEFT),
		ADD(5, Associativity.LEFT),
		SUBTRACT(5, Associativity.LEFT),
		MULTIPLY(6, Associativity.LEFT),
		DIVIDE(6, Associativity.LEFT),
		NEGATE(7, Associativity.RIGHT),
		NOT(7, Associativity.RIGHT),
		POWER(8, Associativity.RIGHT);

		private final int precedence;
		private final Associativity associativity;

		Operator(int precedence, Associativity associativity)
		{
			this.precedence = precedence;
			this.associativity = associativity;
		}

		int getPrecedence()
		{
			return precedence;
		}

		Associativity getAssociativity()
		{
			return associativity;
		}
	}

	static boolean shouldReduce(Operator stackTop, Operator incoming)
	{
		if (stackTop == Operator.SENTINEL || stackTop == Operator.LEFT_PAREN || stackTop == Operator.QUESTION)
		{
			return false;
		}
		if (stackTop.getPrecedence() > incoming.getPrecedence())
		{
			return true;
		}
		return stackTop.getPrecedence() == incoming.getPrecedence()
			&& incoming.getAssociativity() == Associativity.LEFT;
	}
}

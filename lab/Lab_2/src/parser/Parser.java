package parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import exceptions.DividedByZeroException;
import exceptions.EmptyExpressionException;
import exceptions.ExpressionException;
import exceptions.FunctionCallException;
import exceptions.MissingLeftParenthesisException;
import exceptions.MissingOperandException;
import exceptions.MissingOperatorException;
import exceptions.MissingRightParenthesisException;
import exceptions.TrinaryOperationException;
import exceptions.TypeMismatchedException;

/**
 * Shift/reduce parser driven by an operator-precedence table.
 */
final class Parser
{
	private final Scanner scanner;
	private final Deque<OperatorTable.Operator> operators;
	private final Deque<Value> values;
	private final Deque<ParenFrame> parenFrames;
	private boolean expectingOperand;
	private boolean sawToken;

	Parser(Scanner scanner)
	{
		this.scanner = scanner;
		this.operators = new ArrayDeque<OperatorTable.Operator>();
		this.values = new ArrayDeque<Value>();
		this.parenFrames = new ArrayDeque<ParenFrame>();
		this.expectingOperand = true;
		this.sawToken = false;
	}

	Value parse() throws ExpressionException
	{
		operators.push(OperatorTable.Operator.SENTINEL);

		while (true)
		{
			Token token = scanner.next();
			if (token.getType() == TokenType.END)
			{
				break;
			}
			sawToken = true;
			process(token);
		}

		if (!sawToken)
		{
			throw new EmptyExpressionException("Empty expression.");
		}
		if (!parenFrames.isEmpty())
		{
			throw new MissingRightParenthesisException("Missing right parenthesis.");
		}
		if (expectingOperand)
		{
			if (containsPendingQuestion())
			{
				throw new TrinaryOperationException("Question mark and colon do not match.");
			}
			throw new MissingOperandException("Operand expected.");
		}

		while (operators.peek() != OperatorTable.Operator.SENTINEL)
		{
			if (operators.peek() == OperatorTable.Operator.QUESTION)
			{
				throw new TrinaryOperationException("Question mark and colon do not match.");
			}
			reduceTop();
		}

		if (values.size() != 1)
		{
			throw new MissingOperatorException("Operator expected.");
		}

		Value result = values.pop();
		if (result.isBoolean())
		{
			throw new TypeMismatchedException("Final result must be numeric.");
		}
		return result;
	}

	private void process(Token token) throws ExpressionException
	{
		switch (token.getType())
		{
		case NUMBER:
			pushValue(Value.ofNumber(token.getNumberValue()));
			return;
		case BOOLEAN:
			pushValue(Value.ofBoolean(token.getBooleanValue()));
			return;
		case IDENTIFIER:
			startFunctionCall(token);
			return;
		case LPAREN:
			openGroup();
			return;
		case RPAREN:
			closeParenthesis();
			return;
		case COMMA:
			handleComma();
			return;
		case QUESTION:
			handleQuestion();
			return;
		case COLON:
			handleColon();
			return;
		case PLUS:
			pushBinaryOperator(OperatorTable.Operator.ADD);
			return;
		case MINUS:
			if (expectingOperand)
			{
				pushOperator(OperatorTable.Operator.NEGATE);
			}
			else
			{
				pushBinaryOperator(OperatorTable.Operator.SUBTRACT);
			}
			return;
		case STAR:
			pushBinaryOperator(OperatorTable.Operator.MULTIPLY);
			return;
		case SLASH:
			pushBinaryOperator(OperatorTable.Operator.DIVIDE);
			return;
		case CARET:
			pushBinaryOperator(OperatorTable.Operator.POWER);
			return;
		case NOT:
			if (!expectingOperand)
			{
				throw new MissingOperatorException("Operator expected before '!'.");
			}
			pushOperator(OperatorTable.Operator.NOT);
			return;
		case AND:
			pushBinaryOperator(OperatorTable.Operator.AND);
			return;
		case OR:
			pushBinaryOperator(OperatorTable.Operator.OR);
			return;
		case LT:
			pushBinaryOperator(OperatorTable.Operator.LT);
			return;
		case LE:
			pushBinaryOperator(OperatorTable.Operator.LE);
			return;
		case GT:
			pushBinaryOperator(OperatorTable.Operator.GT);
			return;
		case GE:
			pushBinaryOperator(OperatorTable.Operator.GE);
			return;
		case EQ:
			pushBinaryOperator(OperatorTable.Operator.EQ);
			return;
		case NE:
			pushBinaryOperator(OperatorTable.Operator.NE);
			return;
		default:
			throw new MissingOperatorException("Unexpected token: " + token.getText());
		}
	}

	private void pushValue(Value value) throws MissingOperatorException
	{
		if (!expectingOperand)
		{
			throw new MissingOperatorException("Operator expected.");
		}
		values.push(value);
		expectingOperand = false;
	}

	private void startFunctionCall(Token token) throws ExpressionException
	{
		if (!expectingOperand)
		{
			throw new MissingOperatorException("Operator expected before function call.");
		}

		Token next = scanner.peek();
		if (next.getType() != TokenType.LPAREN)
		{
			throw new FunctionCallException("Function call must be followed by '('.");
		}

		scanner.next();
		operators.push(OperatorTable.Operator.LEFT_PAREN);
		parenFrames.push(ParenFrame.function(token.getText()));
		expectingOperand = true;
	}

	private void openGroup() throws MissingOperatorException
	{
		if (!expectingOperand)
		{
			throw new MissingOperatorException("Operator expected before '('.");
		}
		operators.push(OperatorTable.Operator.LEFT_PAREN);
		parenFrames.push(ParenFrame.group());
		expectingOperand = true;
	}

	private void closeParenthesis() throws ExpressionException
	{
		if (parenFrames.isEmpty())
		{
			throw new MissingLeftParenthesisException("Missing left parenthesis.");
		}
		if (expectingOperand)
		{
			throw new MissingOperandException("Operand expected.");
		}

		reduceUntilLeftParenthesis();
		operators.pop();

		ParenFrame frame = parenFrames.pop();
		if (frame.isFunction())
		{
			frame.addArgument(popValue());
			values.push(applyFunction(frame));
		}
		expectingOperand = false;
	}

	private void handleComma() throws ExpressionException
	{
		if (parenFrames.isEmpty() || !parenFrames.peek().isFunction())
		{
			throw new FunctionCallException("Comma is only allowed inside a function argument list.");
		}
		if (expectingOperand)
		{
			throw new MissingOperandException("Operand expected.");
		}

		reduceUntilLeftParenthesis();
		parenFrames.peek().addArgument(popValue());
		expectingOperand = true;
	}

	private void handleQuestion() throws ExpressionException
	{
		if (expectingOperand)
		{
			throw new MissingOperandException("Condition expression expected.");
		}
		pushOperator(OperatorTable.Operator.QUESTION);
		expectingOperand = true;
	}

	private void handleColon() throws ExpressionException
	{
		if (expectingOperand)
		{
			throw new MissingOperandException("Operand expected after ':'.");
		}
		if (!hasQuestionBeforeBoundary())
		{
			throw new TrinaryOperationException("Question mark and colon do not match.");
		}

		while (true)
		{
			OperatorTable.Operator top = operators.peek();
			if (top == OperatorTable.Operator.QUESTION)
			{
				operators.pop();
				operators.push(OperatorTable.Operator.TERNARY);
				expectingOperand = true;
				return;
			}
			if (top == OperatorTable.Operator.LEFT_PAREN || top == OperatorTable.Operator.SENTINEL)
			{
				throw new TrinaryOperationException("Question mark and colon do not match.");
			}
			reduceTop();
		}
	}

	private void pushBinaryOperator(OperatorTable.Operator operator) throws ExpressionException
	{
		if (expectingOperand)
		{
			throw new MissingOperandException("Operand expected.");
		}
		pushOperator(operator);
		expectingOperand = true;
	}

	private void pushOperator(OperatorTable.Operator incoming) throws ExpressionException
	{
		while (OperatorTable.shouldReduce(operators.peek(), incoming))
		{
			reduceTop();
		}
		operators.push(incoming);
	}

	private void reduceUntilLeftParenthesis() throws ExpressionException
	{
		while (true)
		{
			OperatorTable.Operator top = operators.peek();
			if (top == OperatorTable.Operator.LEFT_PAREN)
			{
				return;
			}
			if (top == OperatorTable.Operator.SENTINEL)
			{
				throw new MissingLeftParenthesisException("Missing left parenthesis.");
			}
			if (top == OperatorTable.Operator.QUESTION)
			{
				throw new TrinaryOperationException("Question mark and colon do not match.");
			}
			reduceTop();
		}
	}

	private void reduceTop() throws ExpressionException
	{
		OperatorTable.Operator operator = operators.pop();
		switch (operator)
		{
		case NEGATE:
			values.push(Value.ofNumber(-popValue().asNumber()));
			return;
		case NOT:
			values.push(Value.ofBoolean(!popValue().asBoolean()));
			return;
		case ADD:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofNumber(left + right));
			return;
		}
		case SUBTRACT:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofNumber(left - right));
			return;
		}
		case MULTIPLY:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofNumber(left * right));
			return;
		}
		case DIVIDE:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			if (right == 0.0)
			{
				throw new DividedByZeroException("Division by zero.");
			}
			values.push(Value.ofNumber(left / right));
			return;
		}
		case POWER:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofNumber(Math.pow(left, right)));
			return;
		}
		case LT:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofBoolean(left < right));
			return;
		}
		case LE:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofBoolean(left <= right));
			return;
		}
		case GT:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofBoolean(left > right));
			return;
		}
		case GE:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofBoolean(left >= right));
			return;
		}
		case EQ:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofBoolean(left == right));
			return;
		}
		case NE:
		{
			double right = popValue().asNumber();
			double left = popValue().asNumber();
			values.push(Value.ofBoolean(left != right));
			return;
		}
		case AND:
		{
			boolean right = popValue().asBoolean();
			boolean left = popValue().asBoolean();
			values.push(Value.ofBoolean(left & right));
			return;
		}
		case OR:
		{
			boolean right = popValue().asBoolean();
			boolean left = popValue().asBoolean();
			values.push(Value.ofBoolean(left | right));
			return;
		}
		case TERNARY:
		{
			double falseValue = popValue().asNumber();
			double trueValue = popValue().asNumber();
			boolean condition = popValue().asBoolean();
			values.push(Value.ofNumber(condition ? trueValue : falseValue));
			return;
		}
		default:
			throw new MissingOperatorException("Invalid operator stack state.");
		}
	}

	private Value popValue() throws MissingOperandException
	{
		if (values.isEmpty())
		{
			throw new MissingOperandException("Operand expected.");
		}
		return values.pop();
	}

	private boolean containsPendingQuestion()
	{
		for (OperatorTable.Operator operator : operators)
		{
			if (operator == OperatorTable.Operator.QUESTION)
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasQuestionBeforeBoundary()
	{
		for (OperatorTable.Operator operator : operators)
		{
			if (operator == OperatorTable.Operator.QUESTION)
			{
				return true;
			}
			if (operator == OperatorTable.Operator.LEFT_PAREN || operator == OperatorTable.Operator.SENTINEL)
			{
				return false;
			}
		}
		return false;
	}

	private Value applyFunction(ParenFrame frame) throws ExpressionException
	{
		List<Value> arguments = frame.getArguments();
		String functionName = frame.getFunctionName();

		if ("sin".equals(functionName) || "cos".equals(functionName))
		{
			if (arguments.size() < 1)
			{
				throw new MissingOperandException("Function argument is missing.");
			}
			if (arguments.size() > 1)
			{
				throw new FunctionCallException("Function accepts only one argument.");
			}

			double argument = arguments.get(0).asNumber();
			return "sin".equals(functionName)
				? Value.ofNumber(Math.sin(argument))
				: Value.ofNumber(Math.cos(argument));
		}

		if (arguments.size() < 2)
		{
			throw new MissingOperandException("Function needs at least two arguments.");
		}

		double result = arguments.get(0).asNumber();
		for (int i = 1; i < arguments.size(); i++)
		{
			double value = arguments.get(i).asNumber();
			if ("max".equals(functionName))
			{
				result = Math.max(result, value);
			}
			else
			{
				result = Math.min(result, value);
			}
		}
		return Value.ofNumber(result);
	}

	private static final class ParenFrame
	{
		private final String functionName;
		private final List<Value> arguments;

		private ParenFrame(String functionName)
		{
			this.functionName = functionName;
			this.arguments = functionName == null ? null : new ArrayList<Value>();
		}

		static ParenFrame group()
		{
			return new ParenFrame(null);
		}

		static ParenFrame function(String functionName)
		{
			return new ParenFrame(functionName);
		}

		boolean isFunction()
		{
			return functionName != null;
		}

		String getFunctionName()
		{
			return functionName;
		}

		void addArgument(Value value)
		{
			arguments.add(value);
		}

		List<Value> getArguments()
		{
			return arguments;
		}
	}
}

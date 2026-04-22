/**
 * @Copyright(C) 2008 Software Engineering Laboratory (SELAB), Department of Computer
 * Science, SUN YAT-SEN UNIVERSITY. All rights reserved.
 **/

package parser;

import exceptions.ExpressionException;

/**
 * Main program of the expression based calculator ExprEval.
 *
 * <p>This class keeps the soft device contract unchanged and delegates the real work to the
 * lexical scanner and the operator-precedence parser.
 *
 * @author Lianglihang
 * @version 2.00 (Last update: 2026-04-22)
 **/
public class Calculator
{
	/**
	 * Calculates the value of an input expression.
	 *
	 * @param expression input expression
	 * @return evaluation result
	 * @throws ExpressionException if the input contains lexical, syntactic or semantic errors
	 */
	public double calculate(String expression) throws ExpressionException
	{
		Scanner scanner = new Scanner(expression == null ? "" : expression);
		Parser parser = new Parser(scanner);
		Value result = parser.parse();
		return result.asNumber();
	}
}

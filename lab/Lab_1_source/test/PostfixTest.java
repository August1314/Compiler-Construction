import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostfixTest {
	@Test
	public void testCase001() throws IOException {
		ParseResult result = parse("9-5+2\n");
		assertEquals("95-2+", result.getPostfix());
		assertTrue(!result.hasErrors());
	}

	@Test
	public void testCase002() throws IOException {
		ParseResult result = parse("1-2+3-4+5-6+7-8+9-0\n");
		assertEquals("12-3+4-5+6-7+8-9+0-", result.getPostfix());
		assertTrue(!result.hasErrors());
	}

	@Test
	public void testMissingOperator() throws IOException {
		ParseResult result = parse("95+2\n");
		assertTrue(result.hasErrors());
	}

	@Test
	public void testMissingOperand() throws IOException {
		ParseResult result = parse("9-5+-2\n");
		assertTrue(result.hasErrors());
	}

	@Test
	public void testWhitespaceIsError() throws IOException {
		ParseResult result = parse("1 +2\n");
		assertTrue(result.hasErrors());
	}

	@Test
	public void testIllegalCharIsError() throws IOException {
		ParseResult result = parse("1a+2\n");
		assertTrue(result.hasErrors());
	}

	private ParseResult parse(String input) throws IOException {
		Parser parser = new Parser(new StringReader(input), false);
		return parser.parse();
	}
}

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Compare recursive and iterative parsing performance on generated inputs.
 */
public class PerfCompare {
	private static final int DEFAULT_ROUNDS = 5;
	private static final int DEFAULT_EXPR_COUNT = 2000;
	private static final int DEFAULT_LEN = 64;

	public static void main(String[] args) throws IOException {
		int rounds = DEFAULT_ROUNDS;
		int exprCount = DEFAULT_EXPR_COUNT;
		int length = DEFAULT_LEN;
		boolean csv = false;
		long seed = 7L;
		for (int i = 0; i < args.length; i++) {
			if ("--rounds".equals(args[i]) && i + 1 < args.length) {
				rounds = Integer.parseInt(args[++i]);
			} else if ("--count".equals(args[i]) && i + 1 < args.length) {
				exprCount = Integer.parseInt(args[++i]);
			} else if ("--length".equals(args[i]) && i + 1 < args.length) {
				length = Integer.parseInt(args[++i]);
			} else if ("--csv".equals(args[i])) {
				csv = true;
			} else if ("--seed".equals(args[i]) && i + 1 < args.length) {
				seed = Long.parseLong(args[++i]);
			}
		}

		Random random = new Random(seed);
		List<String> inputs = new ArrayList<String>();
		for (int i = 0; i < exprCount; i++) {
			inputs.add(generateExpr(random, length));
		}

		long recursiveTotal = 0L;
		long iterativeTotal = 0L;
		for (int r = 0; r < rounds; r++) {
			recursiveTotal += timeRecursive(inputs);
			iterativeTotal += timeIterative(inputs);
		}

		double recursiveAvgMs = recursiveTotal / 1_000_000.0 / rounds;
		double iterativeAvgMs = iterativeTotal / 1_000_000.0 / rounds;
		if (csv) {
			System.out.println("rounds,exprCount,length,recursive_ms,iterative_ms");
			System.out.println(rounds + "," + exprCount + "," + length + "," +
					recursiveAvgMs + "," + iterativeAvgMs);
			return;
		}
		System.out.println("Rounds: " + rounds + ", expressions: " + exprCount + ", length: " + length);
		System.out.println("Recursive avg (ms): " + recursiveAvgMs);
		System.out.println("Iterative avg (ms): " + iterativeAvgMs);
	}

	private static long timeRecursive(List<String> inputs) throws IOException {
		long start = System.nanoTime();
		for (int i = 0; i < inputs.size(); i++) {
			Reader reader = new StringReader(inputs.get(i));
			RecursiveParser parser = new RecursiveParser(reader);
			parser.expr();
		}
		return System.nanoTime() - start;
	}

	private static long timeIterative(List<String> inputs) throws IOException {
		long start = System.nanoTime();
		for (int i = 0; i < inputs.size(); i++) {
			Reader reader = new StringReader(inputs.get(i));
			Parser parser = new Parser(reader, false);
			parser.parse();
		}
		return System.nanoTime() - start;
	}

	private static String generateExpr(Random random, int length) {
		StringBuilder builder = new StringBuilder();
		builder.append((char) ('0' + random.nextInt(10)));
		for (int i = 1; i < length; i++) {
			char op = random.nextBoolean() ? '+' : '-';
			builder.append(op);
			builder.append((char) ('0' + random.nextInt(10)));
		}
		builder.append('\n');
		return builder.toString();
	}
}

/**
 * Original recursive parser for performance comparison.
 */
class RecursiveParser {
	private final Reader reader;
	private int lookahead;

	public RecursiveParser(Reader reader) throws IOException {
		this.reader = reader;
		this.lookahead = reader.read();
	}

	void expr() throws IOException {
		term();
		rest();
	}

	void rest() throws IOException {
		if (lookahead == '+') {
			match('+');
			term();
			rest();
		} else if (lookahead == '-') {
			match('-');
			term();
			rest();
		}
	}

	void term() throws IOException {
		if (lookahead >= '0' && lookahead <= '9') {
			match(lookahead);
		} else {
			throw new Error("syntax error");
		}
	}

	void match(int t) throws IOException {
		if (lookahead == t) {
			lookahead = reader.read();
		} else {
			throw new Error("syntax error");
		}
	}
}

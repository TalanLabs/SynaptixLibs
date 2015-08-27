import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.antlr.EvalGraphCalcVisitor;
import com.synaptix.taskmanager.antlr.GraphCalcLexer;
import com.synaptix.taskmanager.antlr.GraphCalcParser;
import com.synaptix.taskmanager.antlr.GraphCalcParser.CompileContext;
import com.synaptix.taskmanager.antlr.ThrowingErrorListener;

public class GraphCalcTest {

	private CompileContext compile(String rule) throws ParseCancellationException {
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream(rule));
		lex.removeErrorListeners();
		lex.addErrorListener(ThrowingErrorListener.INSTANCE);
		CommonTokenStream input = new CommonTokenStream(lex);
		GraphCalcParser parser = new GraphCalcParser(input);
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		return parser.compile();

	}

	private void verifySuccess(String rule) {
		try {
			compile(rule);
		} catch (ParseCancellationException e) {
			Assert.fail();
		}
	}

	private void verifyFail(String rule) {
		try {
			compile(rule);
			Assert.fail();
		} catch (ParseCancellationException e) {
		}
	}

	private String toGraphCalcString(String rule) {
		return new EvalGraphCalcVisitor().visit(compile(rule)).toString();
	}

	private void verifySame(String expected, String actual) {
		Assert.assertEquals(expected, toGraphCalcString(actual));
	}

	@Test
	public void syntax() {
		verifySuccess("1");
		verifySuccess("123");
		verifySuccess("1-2_3");
		verifySuccess("1,2,3");
		verifySuccess("1=>2");
		verifySuccess("1|2|3");
		verifySuccess("(1)");
		verifySuccess("(1=>2)");
		verifySuccess("1=>2,3");
		verifySuccess("1=>2,(3=>4)");
		verifySuccess("1=>2|3=>4");
		verifySuccess("((1=>2)|(3=>4),5=>6)=>7");

		verifyFail("abc&");
		verifyFail("1=2");
		verifyFail("1;2");
	}

	@Test
	public void parse() {
		verifySame("1", "1");
		verifySame("(1,2,3)", "1,2,3");
		verifySame("(1|2|3)", "1|2|3");
		verifySame("(1=>2)", "1=>2");
		verifySame("((1=>2)=>3)", "1=>2=>3");
		verifySame("((1=>2),3)", "1=>2,3");
		verifySame("(1,(2=>3))", "1,2=>3");
		verifySame("((1=>2)|3)", "1=>2|3");
		verifySame("(1|(2,3))", "1|2,3");

	}
}

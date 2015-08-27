package antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.synaptix.taskmanager.antlr.EvalGraphCalcVisitor;
import com.synaptix.taskmanager.antlr.GraphCalcLexer;
import com.synaptix.taskmanager.antlr.GraphCalcParser;
import com.synaptix.taskmanager.antlr.GraphCalcParser.CompileContext;

public class MainAntlr {

	public static void main(String[] args) {
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("1,2|3"));
		CommonTokenStream input = new CommonTokenStream(lex);
		GraphCalcParser parser = new GraphCalcParser(input);
		CompileContext c = parser.compile();

		System.out.println(c.exception);
		System.out.println(new EvalGraphCalcVisitor().visit(c));

	}
}

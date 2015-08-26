package com.synaptix.taskmanager.antlr;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class GraphCalcHelper {

	/**
	 * Test if valid id
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isValidId(String id) {
		return id.matches("[a-zA-Z0-9_-]+");
	}

	/**
	 * Test if valid graph rule
	 * 
	 * @param graphRule
	 * @return
	 */
	public static boolean isValidGraphRule(String graphRule) {
		if (graphRule == null) {
			return true;
		}
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRStringStream(graphRule));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		RecognizerSharedState recognizerSharedState = new RecognizerSharedState();
		GraphCalcParser parser = new GraphCalcParser(tokens, recognizerSharedState);

		try {
			parser.compile();
			return recognizerSharedState.syntaxErrors == 0;
		} catch (RecognitionException e) {
			return false;
		}
	}

	/**
	 * Extract id from graph rule
	 * 
	 * @param graphRule
	 * @return
	 */
	public static List<String> extractId(String graphRule) {
		if (graphRule == null) {
			return null;
		}
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRStringStream(graphRule));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		RecognizerSharedState recognizerSharedState = new RecognizerSharedState();
		GraphCalcParser parser = new GraphCalcParser(tokens, recognizerSharedState);

		try {
			AbstractGraphNode node = parser.compile();
			return extractId(node);
		} catch (RecognitionException e) {
			return null;
		}
	}

	private static List<String> extractId(AbstractGraphNode node) {
		List<String> res = new ArrayList<String>();
		if (node instanceof IdGraphNode) {
			res.add(((IdGraphNode) node).getId());
		} else if (node instanceof ParallelGraphNode) {
			for (AbstractGraphNode subNode : ((ParallelGraphNode) node).getNodes()) {
				res.addAll(extractId(subNode));
			}
		} else if (node instanceof NextGraphNode) {
			res.addAll(extractId(((NextGraphNode) node).getFirstNode()));
			res.addAll(extractId(((NextGraphNode) node).getNextNode()));
		}
		return res;
	}

	/**
	 * Get a abstract node for graph rule
	 * 
	 * @param graphRule
	 * @return
	 */
	public static final AbstractGraphNode buildGraphRule(String graphRule) {
		if (graphRule == null) {
			return null;
		}
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRStringStream(graphRule));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		RecognizerSharedState recognizerSharedState = new RecognizerSharedState();
		GraphCalcParser parser = new GraphCalcParser(tokens, recognizerSharedState);

		try {
			return parser.compile();
		} catch (RecognitionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Replace Id for other Id
	 * 
	 * @param graphRule
	 * @param replaceId
	 * @return
	 */
	public static final String replaceId(String graphRule, IReplaceId replaceId) {
		if (graphRule == null) {
			return null;
		}
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRStringStream(graphRule));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		RecognizerSharedState recognizerSharedState = new RecognizerSharedState();
		GraphCalcParser parser = new GraphCalcParser(tokens, recognizerSharedState);

		try {
			AbstractGraphNode node = parser.compile();
			return toString(replaceId(node, replaceId));
		} catch (RecognitionException e) {
			return null;
		}
	}

	public interface IReplaceId {

		public String getOtherId(String id);

	}

	private static final AbstractGraphNode replaceId(AbstractGraphNode node, IReplaceId replaceId) {
		AbstractGraphNode res = null;
		if (node instanceof IdGraphNode) {
			res = new IdGraphNode(replaceId.getOtherId(((IdGraphNode) node).getId()));
		} else if (node instanceof ParallelGraphNode) {
			List<AbstractGraphNode> ns = new ArrayList<AbstractGraphNode>();
			for (AbstractGraphNode subNode : ((ParallelGraphNode) node).getNodes()) {
				ns.add(replaceId(subNode, replaceId));
			}
			res = new ParallelGraphNode(ns);
		} else if (node instanceof NextGraphNode) {
			AbstractGraphNode firstNode = replaceId(((NextGraphNode) node).getFirstNode(), replaceId);
			AbstractGraphNode nextNode = replaceId(((NextGraphNode) node).getNextNode(), replaceId);
			res = new NextGraphNode(firstNode, nextNode);
		}
		return res;
	}

	public static final String toString(AbstractGraphNode node) {
		StringBuilder sb = new StringBuilder();
		if (node instanceof IdGraphNode) {
			sb.append(((IdGraphNode) node).getId());
		} else if (node instanceof ParallelGraphNode) {
			ParallelGraphNode pgn = (ParallelGraphNode) node;
			boolean first = true;
			for (AbstractGraphNode subNode : pgn.getNodes()) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(toString(subNode));
			}
		} else if (node instanceof NextGraphNode) {
			NextGraphNode ngn = (NextGraphNode) node;
			AbstractGraphNode fisrtNode = ngn.getFirstNode();
			if (!(fisrtNode instanceof IdGraphNode)) {
				sb.append("(");
			}
			sb.append(toString(fisrtNode));
			if (!(fisrtNode instanceof IdGraphNode)) {
				sb.append(")");
			}
			sb.append("=>");
			AbstractGraphNode nextNode = ngn.getNextNode();
			if (!(nextNode instanceof IdGraphNode)) {
				sb.append("(");
			}
			sb.append(toString(nextNode));
			if (!(nextNode instanceof IdGraphNode)) {
				sb.append(")");
			}
		}
		return sb.toString();
	}
}

// Generated from com\synaptix\taskmanager\antlr\GraphCalc.g4 by ANTLR 4.5.1
package com.synaptix.taskmanager.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GraphCalcLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NEXT=1, AND=2, OR=3, LPAREN=4, RPAREN=5, ID=6;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"NEXT", "AND", "OR", "LPAREN", "RPAREN", "ID"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'=>'", "','", "'|'", "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "NEXT", "AND", "OR", "LPAREN", "RPAREN", "ID"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public GraphCalcLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "GraphCalc.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\b\37\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3"+
		"\5\3\5\3\6\3\6\3\7\6\7\34\n\7\r\7\16\7\35\2\2\b\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\3\2\3\7\2//\62;C\\aac|\37\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\3\17\3\2\2\2\5\22\3\2\2\2\7\24\3\2\2\2"+
		"\t\26\3\2\2\2\13\30\3\2\2\2\r\33\3\2\2\2\17\20\7?\2\2\20\21\7@\2\2\21"+
		"\4\3\2\2\2\22\23\7.\2\2\23\6\3\2\2\2\24\25\7~\2\2\25\b\3\2\2\2\26\27\7"+
		"*\2\2\27\n\3\2\2\2\30\31\7+\2\2\31\f\3\2\2\2\32\34\t\2\2\2\33\32\3\2\2"+
		"\2\34\35\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\16\3\2\2\2\4\2\35\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
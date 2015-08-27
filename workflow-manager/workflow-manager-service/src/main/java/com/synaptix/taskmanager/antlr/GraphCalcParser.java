// Generated from com\synaptix\taskmanager\antlr\GraphCalc.g4 by ANTLR 4.5.1
package com.synaptix.taskmanager.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GraphCalcParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NEXT=1, AND=2, OR=3, LPAREN=4, RPAREN=5, ID=6;
	public static final int
		RULE_compile = 0, RULE_expr = 1, RULE_exprOr = 2, RULE_exprAnd = 3, RULE_exprNext = 4, 
		RULE_factor = 5;
	public static final String[] ruleNames = {
		"compile", "expr", "exprOr", "exprAnd", "exprNext", "factor"
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

	@Override
	public String getGrammarFileName() { return "GraphCalc.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GraphCalcParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CompileContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(GraphCalcParser.EOF, 0); }
		public CompileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compile; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitCompile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompileContext compile() throws RecognitionException {
		CompileContext _localctx = new CompileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compile);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			expr();
			setState(13);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprAndContext exprAnd() {
			return getRuleContext(ExprAndContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			exprAnd();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprOrContext extends ParserRuleContext {
		public List<ExprAndContext> exprAnd() {
			return getRuleContexts(ExprAndContext.class);
		}
		public ExprAndContext exprAnd(int i) {
			return getRuleContext(ExprAndContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(GraphCalcParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(GraphCalcParser.OR, i);
		}
		public ExprOrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprOr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitExprOr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprOrContext exprOr() throws RecognitionException {
		ExprOrContext _localctx = new ExprOrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_exprOr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			exprAnd();
			setState(22);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(18);
				match(OR);
				setState(19);
				exprAnd();
				}
				}
				setState(24);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprAndContext extends ParserRuleContext {
		public List<ExprNextContext> exprNext() {
			return getRuleContexts(ExprNextContext.class);
		}
		public ExprNextContext exprNext(int i) {
			return getRuleContext(ExprNextContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(GraphCalcParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(GraphCalcParser.AND, i);
		}
		public ExprAndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprAnd; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitExprAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprAndContext exprAnd() throws RecognitionException {
		ExprAndContext _localctx = new ExprAndContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_exprAnd);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			exprNext();
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(26);
				match(AND);
				setState(27);
				exprNext();
				}
				}
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprNextContext extends ParserRuleContext {
		public FactorContext first;
		public List<FactorContext> factor() {
			return getRuleContexts(FactorContext.class);
		}
		public FactorContext factor(int i) {
			return getRuleContext(FactorContext.class,i);
		}
		public List<TerminalNode> NEXT() { return getTokens(GraphCalcParser.NEXT); }
		public TerminalNode NEXT(int i) {
			return getToken(GraphCalcParser.NEXT, i);
		}
		public ExprNextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprNext; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitExprNext(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprNextContext exprNext() throws RecognitionException {
		ExprNextContext _localctx = new ExprNextContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_exprNext);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			((ExprNextContext)_localctx).first = factor();
			setState(38);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(34);
				match(NEXT);
				setState(35);
				factor();
				}
				}
				setState(40);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FactorContext extends ParserRuleContext {
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
	 
		public FactorContext() { }
		public void copyFrom(FactorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ParensContext extends FactorContext {
		public TerminalNode LPAREN() { return getToken(GraphCalcParser.LPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(GraphCalcParser.RPAREN, 0); }
		public ParensContext(FactorContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitParens(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IdContext extends FactorContext {
		public TerminalNode ID() { return getToken(GraphCalcParser.ID, 0); }
		public IdContext(FactorContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphCalcVisitor ) return ((GraphCalcVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_factor);
		try {
			setState(46);
			switch (_input.LA(1)) {
			case ID:
				_localctx = new IdContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(41);
				match(ID);
				}
				break;
			case LPAREN:
				_localctx = new ParensContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(42);
				match(LPAREN);
				setState(43);
				expr();
				setState(44);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\b\63\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\7"+
		"\4\27\n\4\f\4\16\4\32\13\4\3\5\3\5\3\5\7\5\37\n\5\f\5\16\5\"\13\5\3\6"+
		"\3\6\3\6\7\6\'\n\6\f\6\16\6*\13\6\3\7\3\7\3\7\3\7\3\7\5\7\61\n\7\3\7\2"+
		"\2\b\2\4\6\b\n\f\2\2\60\2\16\3\2\2\2\4\21\3\2\2\2\6\23\3\2\2\2\b\33\3"+
		"\2\2\2\n#\3\2\2\2\f\60\3\2\2\2\16\17\5\4\3\2\17\20\7\2\2\3\20\3\3\2\2"+
		"\2\21\22\5\b\5\2\22\5\3\2\2\2\23\30\5\b\5\2\24\25\7\5\2\2\25\27\5\b\5"+
		"\2\26\24\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\7\3\2\2"+
		"\2\32\30\3\2\2\2\33 \5\n\6\2\34\35\7\4\2\2\35\37\5\n\6\2\36\34\3\2\2\2"+
		"\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!\t\3\2\2\2\" \3\2\2\2#(\5\f\7\2$%"+
		"\7\3\2\2%\'\5\f\7\2&$\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2\2)\13\3\2\2"+
		"\2*(\3\2\2\2+\61\7\b\2\2,-\7\6\2\2-.\5\4\3\2./\7\7\2\2/\61\3\2\2\2\60"+
		"+\3\2\2\2\60,\3\2\2\2\61\r\3\2\2\2\6\30 (\60";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
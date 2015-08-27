// $ANTLR 3.5 com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g 2015-08-27 10:20:02
 package com.synaptix.taskmanager.antlr; 

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class GraphCalcOldLexer extends Lexer {
	public static final int EOF=-1;
	public static final int ID=4;
	public static final int LPAREN=5;
	public static final int NEXT=6;
	public static final int PARALLEL=7;
	public static final int RPAREN=8;
	public static final int WHITESPACE=9;

	  @Override
	  public void emitErrorMessage(String msg) {
	  }


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public GraphCalcOldLexer() {} 
	public GraphCalcOldLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public GraphCalcOldLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g"; }

	// $ANTLR start "LPAREN"
	public final void mLPAREN() throws RecognitionException {
		try {
			int _type = LPAREN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:9:8: ( '(' )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:9:10: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LPAREN"

	// $ANTLR start "NEXT"
	public final void mNEXT() throws RecognitionException {
		try {
			int _type = NEXT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:10:6: ( '=>' )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:10:8: '=>'
			{
			match("=>"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NEXT"

	// $ANTLR start "PARALLEL"
	public final void mPARALLEL() throws RecognitionException {
		try {
			int _type = PARALLEL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:11:10: ( ',' )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:11:12: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PARALLEL"

	// $ANTLR start "RPAREN"
	public final void mRPAREN() throws RecognitionException {
		try {
			int _type = RPAREN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:12:8: ( ')' )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:12:10: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RPAREN"

	// $ANTLR start "WHITESPACE"
	public final void mWHITESPACE() throws RecognitionException {
		try {
			int _type = WHITESPACE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:43:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:43:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			{
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:43:14: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '\t' && LA1_0 <= '\n')||(LA1_0 >= '\f' && LA1_0 <= '\r')||LA1_0==' ') ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			 _channel = HIDDEN; 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WHITESPACE"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:45:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )+ )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:45:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )+
			{
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:45:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0=='-'||(LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:
					{
					if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	@Override
	public void mTokens() throws RecognitionException {
		// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:8: ( LPAREN | NEXT | PARALLEL | RPAREN | WHITESPACE | ID )
		int alt3=6;
		switch ( input.LA(1) ) {
		case '(':
			{
			alt3=1;
			}
			break;
		case '=':
			{
			alt3=2;
			}
			break;
		case ',':
			{
			alt3=3;
			}
			break;
		case ')':
			{
			alt3=4;
			}
			break;
		case '\t':
		case '\n':
		case '\f':
		case '\r':
		case ' ':
			{
			alt3=5;
			}
			break;
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case '_':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			{
			alt3=6;
			}
			break;
		default:
			NoViableAltException nvae =
				new NoViableAltException("", 3, 0, input);
			throw nvae;
		}
		switch (alt3) {
			case 1 :
				// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:10: LPAREN
				{
				mLPAREN(); 

				}
				break;
			case 2 :
				// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:17: NEXT
				{
				mNEXT(); 

				}
				break;
			case 3 :
				// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:22: PARALLEL
				{
				mPARALLEL(); 

				}
				break;
			case 4 :
				// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:31: RPAREN
				{
				mRPAREN(); 

				}
				break;
			case 5 :
				// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:38: WHITESPACE
				{
				mWHITESPACE(); 

				}
				break;
			case 6 :
				// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:1:49: ID
				{
				mID(); 

				}
				break;

		}
	}



}

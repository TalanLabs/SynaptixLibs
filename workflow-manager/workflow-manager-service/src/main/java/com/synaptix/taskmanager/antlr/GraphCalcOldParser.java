// $ANTLR 3.5 com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g 2015-08-27 10:20:01
 package com.synaptix.taskmanager.antlr; 

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class GraphCalcOldParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "LPAREN", "NEXT", "PARALLEL", 
		"RPAREN", "WHITESPACE"
	};
	public static final int EOF=-1;
	public static final int ID=4;
	public static final int LPAREN=5;
	public static final int NEXT=6;
	public static final int PARALLEL=7;
	public static final int RPAREN=8;
	public static final int WHITESPACE=9;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public GraphCalcOldParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public GraphCalcOldParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return GraphCalcOldParser.tokenNames; }
	@Override public String getGrammarFileName() { return "com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g"; }


	  @Override
	  public void emitErrorMessage(String msg) {
	  }



	// $ANTLR start "compile"
	// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:31:1: compile returns [AbstractGraphNode res] : a= expr EOF ;
	public final AbstractGraphNode compile() throws RecognitionException {
		AbstractGraphNode res = null;


		AbstractGraphNode a =null;

		try {
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:31:40: (a= expr EOF )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:31:42: a= expr EOF
			{
			pushFollow(FOLLOW_expr_in_compile114);
			a=expr();
			state._fsp--;

			match(input,EOF,FOLLOW_EOF_in_compile116); 
			 res =a; 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return res;
	}
	// $ANTLR end "compile"



	// $ANTLR start "expr"
	// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:33:1: expr returns [AbstractGraphNode value] :a= term ( PARALLEL b= term )* ;
	public final AbstractGraphNode expr() throws RecognitionException {
		AbstractGraphNode value = null;


		AbstractGraphNode a =null;
		AbstractGraphNode b =null;

		try {
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:33:38: (a= term ( PARALLEL b= term )* )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:33:40: a= term ( PARALLEL b= term )*
			{
			 List<AbstractGraphNode> nodes = new ArrayList<AbstractGraphNode>(); 
			pushFollow(FOLLOW_term_in_expr134);
			a=term();
			state._fsp--;

			 nodes.add(a); 
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:33:137: ( PARALLEL b= term )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==PARALLEL) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:33:139: PARALLEL b= term
					{
					match(input,PARALLEL,FOLLOW_PARALLEL_in_expr140); 
					pushFollow(FOLLOW_term_in_expr144);
					b=term();
					state._fsp--;

					 nodes.add(b); 
					}
					break;

				default :
					break loop1;
				}
			}

			 value =nodes.size() == 1 ? nodes.get(0) : new ParallelGraphNode(nodes); 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "expr"



	// $ANTLR start "term"
	// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:35:1: term returns [AbstractGraphNode value] : a= factor ( NEXT b= factor )? ;
	public final AbstractGraphNode term() throws RecognitionException {
		AbstractGraphNode value = null;


		AbstractGraphNode a =null;
		AbstractGraphNode b =null;

		try {
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:35:38: (a= factor ( NEXT b= factor )? )
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:35:40: a= factor ( NEXT b= factor )?
			{
			pushFollow(FOLLOW_factor_in_term163);
			a=factor();
			state._fsp--;

			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:35:49: ( NEXT b= factor )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==NEXT) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:35:51: NEXT b= factor
					{
					match(input,NEXT,FOLLOW_NEXT_in_term167); 
					pushFollow(FOLLOW_factor_in_term171);
					b=factor();
					state._fsp--;

					}
					break;

			}

			 value =b == null ? a : new NextGraphNode(a,b); 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "term"



	// $ANTLR start "factor"
	// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:37:1: factor returns [AbstractGraphNode value] : ( ID | ( LPAREN a= expr RPAREN ) );
	public final AbstractGraphNode factor() throws RecognitionException {
		AbstractGraphNode value = null;


		Token ID1=null;
		AbstractGraphNode a =null;

		try {
			// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:37:40: ( ID | ( LPAREN a= expr RPAREN ) )
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==ID) ) {
				alt3=1;
			}
			else if ( (LA3_0==LPAREN) ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:37:42: ID
					{
					ID1=(Token)match(input,ID,FOLLOW_ID_in_factor187); 
					 value =new IdGraphNode(ID1.getText()); 
					}
					break;
				case 2 :
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:37:90: ( LPAREN a= expr RPAREN )
					{
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:37:90: ( LPAREN a= expr RPAREN )
					// com\\synaptix\\taskmanager\\antlr\\GraphCalcOld.g:37:92: LPAREN a= expr RPAREN
					{
					match(input,LPAREN,FOLLOW_LPAREN_in_factor195); 
					pushFollow(FOLLOW_expr_in_factor199);
					a=expr();
					state._fsp--;

					match(input,RPAREN,FOLLOW_RPAREN_in_factor201); 
					}

					 value =a; 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "factor"

	// Delegated rules



	public static final BitSet FOLLOW_expr_in_compile114 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_compile116 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_term_in_expr134 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_PARALLEL_in_expr140 = new BitSet(new long[]{0x0000000000000030L});
	public static final BitSet FOLLOW_term_in_expr144 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_factor_in_term163 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_NEXT_in_term167 = new BitSet(new long[]{0x0000000000000030L});
	public static final BitSet FOLLOW_factor_in_term171 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_factor187 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_factor195 = new BitSet(new long[]{0x0000000000000030L});
	public static final BitSet FOLLOW_expr_in_factor199 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_RPAREN_in_factor201 = new BitSet(new long[]{0x0000000000000002L});
}

grammar GraphCalcOld;

tokens {
    NEXT    = '=>' ;
    PARALLEL   = ',' ;
    LPAREN = '(' ;
    RPAREN = ')' ;
}

@lexer::header { package com.synaptix.taskmanager.antlr; }

@parser::header { package com.synaptix.taskmanager.antlr; }

@lexer::members {
  @Override
  public void emitErrorMessage(String msg) {
  }
}
 
//override generated method in parser
@parser::members {
  @Override
  public void emitErrorMessage(String msg) {
  }
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

compile returns[AbstractGraphNode res] : a=expr EOF { $res=a; } ; 

expr returns[AbstractGraphNode value]: { List<AbstractGraphNode> nodes = new ArrayList<AbstractGraphNode>(); } a=term { nodes.add(a); } ( PARALLEL b=term { nodes.add(b); } )* { $value=nodes.size() == 1 ? nodes.get(0) : new ParallelGraphNode(nodes); };

term returns[AbstractGraphNode value]: a=factor ( NEXT b=factor )? { $value=b == null ? a : new NextGraphNode(a,b); };
 
factor returns[AbstractGraphNode value]: ID { $value=new IdGraphNode($ID.getText()); } | ( LPAREN a=expr RPAREN ) { $value=a; };
 
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
 
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+    { $channel = HIDDEN; } ;

ID  :   ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')+ ;
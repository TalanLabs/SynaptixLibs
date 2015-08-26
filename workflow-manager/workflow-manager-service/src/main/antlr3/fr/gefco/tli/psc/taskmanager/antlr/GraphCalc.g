grammar GraphCalc;

tokens {
    NEXT    = '=>' ;
    PARALLEL   = ',' ;
    LPAREN = '(' ;
    RPAREN = ')' ;
}

@lexer::header { package fr.gefco.tli.psc.taskmanager.antlr; }

@parser::header { package fr.gefco.tli.psc.taskmanager.antlr; import com.synaptix.entity.IEntity; }

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

compile returns[AbstractNode res] : a=expr EOF { $res=a; } ; 

expr returns[AbstractNode value]: { List<AbstractNode> nodes = new ArrayList<AbstractNode>(); } a=term { nodes.add(a); } ( PARALLEL b=term { nodes.add(b); } )* { $value=nodes.size() == 1 ? nodes.get(0) : new ParallelNode(nodes); };

term returns[AbstractNode value]: a=factor ( NEXT b=factor )? { $value=b == null ? a : new NextNode(a,b); };
 
factor returns[AbstractNode value]: ID { $value=new IdNode($ID.getText()); } | ( LPAREN a=expr RPAREN ) { $value=a; };
 
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
 
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+    { $channel = HIDDEN; } ;

ID  :   ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')+ ;
grammar GraphCalc;

compile: expr EOF
       ;

// A changer pour activer le Or
expr: exprAnd
    ;

exprOr: exprAnd (OR exprAnd)*
      ;

exprAnd: exprNext (AND exprNext)*
       ;

exprNext: first=factor (NEXT factor)*
    ;

factor: ID                 # id
      | LPAREN expr RPAREN # parens
      ;

NEXT : '=>' ;
AND : ',' ;
OR : '|' ;
LPAREN : '(' ;
RPAREN : ')' ;
ID : ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')+ ;

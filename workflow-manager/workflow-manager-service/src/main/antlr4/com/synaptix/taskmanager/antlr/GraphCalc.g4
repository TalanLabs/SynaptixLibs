grammar GraphCalc;

compile: expr EOF
       ;

expr: exprOr
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

grammar Ida;

program
	: (statement)*
	;

statement
	: functionDefinition
	| variableDefinition
	| assignmentStatement
	| ifStatement
	| printStatement
	| whileStatement
	| block
	| structDefinition
	;


functionDefinition
	: 'fn' NAME '('(parameter (',' parameter)*)? ')' (':' typeSpecifier)? block
	;


block
    : '{' (statement)* '}'
    ;

structDefinition
    : 'st' NAME block
    ;

parameter
    :   NAME ':' typeSpecifier
    :   NAME '(' expression ')'
    ;

variableDefinition
    :   parameter ('=' expression)?
    ;

assignmentStatement
    :   NAME '=' expression
    |   fieldAccess '=' expression
    ;

ifStatement
    :   'if' expression block ('else' block)?
    ;

whileStatement
    : 'while' expression block
    ;

printStatement
    :   'print' expression
    ;

expression
    :   booleanExpression
    ;

booleanExpression
    : equalityExpression (('&&' | '||') equalityExpression)*
    ;

equalityExpression
    : relationalExpression (('==' | '!=') relationalExpression)*
    ;

relationalExpression
    : additiveExpression (('<' | '>' | '<=' | '>=') additiveExpression)?
    ;

additiveExpression
    : multiplicativeExpression (('+' | '-') multiplicativeExpression)*
    ;

multiplicativeExpression
    : primaryExpression (('*' | '/' | '%') primaryExpression)*
    ;

primaryExpression
    : primary
    | functionCall
    | fieldAccess
    | arrayAccess
    ;

arrayAccess
    : primaryExpression '[' expression ']'
    ;

fieldAccess
    : primary '.' NAME
    | primary '.' functionCall
    ;

primary
    :   NAME
    |   NUM
    |   STR
    |   BOOL
    |   THIS
    |   arrayExpr
    |   '(' expression ')'
    ;

arrayExpr
    : '[' (expression (',' expression)*)? ']'

functionCall
    : NAME '(' (expression (',' expression)*)? ')'
    ;


arrayTypeSpecifier
    : typeSpecifier '[]'
    ;

typeSpecifier
    :   'int'
    |   'float'
    |   'string'
    |   'bool'
    ;

NAME  :   [a-zA-Z_][a-zA-Z_0-9]*;
NUM :     -?[0-9]+ '.' [0-9]+;
STR :  "\"(\.|[^\\"])*\"";
BOOL : 'true' | 'false'
THIS : 'this'
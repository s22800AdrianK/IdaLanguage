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
	| block
	;

functionDefinition
	: 'fn' NAME '('(parameter (',' parameter)*)? ')' ':' typeSpecifier block
	;


block
    : '{' (statement)* '}'
    ;

parameter
    :   NAME ':' typeSpecifier
    ;

variableDefinition
    :   parameter ('=' expression)?
    ;

assignmentStatement
    :   NAME '=' expression
    ;


ifStatement
    :   'if' expression ':' '{' (statement)* '}' ('else' ':' '{' (statement)* '}')?
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
    ;

primary
    :   NAME
    |   INT
    |   FLOAT
    |   STRING
    |   '(' expression ')'
    ;

functionCall
    : NAME '(' (expression (',' expression)*)? ')'
    ;

typeSpecifier
    :   'int'
    |   'float'
    |   'string'
    |   'bool'
    ;

NAME  :   [a-zA-Z_][a-zA-Z_0-9]*;
INT   :   [0-9]+;
FLOAT :   [0-9]+ '.' [0-9]+;
STRING :  "\"(\.|[^\\"])*\"";

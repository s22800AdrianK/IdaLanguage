package org.example.parser;

import org.example.lexer.Lexer;
import org.example.token.TokenType;

public class IdaParser extends Parser{

    public IdaParser(Lexer lexer, int bufferSize) {
        super(lexer, bufferSize);
    }

    public void program() {
        while (LA(1) != TokenType.EOF_TYPE){
            statement();
        }
    }

    private void statement() {
        switch(LA(1)) {
            case FN -> functionStatement();
            case NAME -> {
                if (LA(2) == TokenType.EQUALS || LA(2) == TokenType.COLON) {
                    variable();
                } else {
                    expressionStatement();
                }
            }
            case IF -> ifStatement();
            case PRINT -> printStatement();
            default -> {
                if (isExpressionStart(LA(1))) {
                    expressionStatement();
                } else {
                    throw new RuntimeException("expecting statement; found " + LT(1));
                }
            }
        }
    }

    private boolean isExpressionStart(TokenType type) {
        return type == TokenType.NAME
                || type == TokenType.NUMBER
                || type == TokenType.STRING
                || type == TokenType.L_BRACK;
    }
    private void printStatement() {
    }

    private void ifStatement() {
    }

    private void expressionStatement() {
        expression();
    }

    private void expression() {
        booleanExpression();
    }

    private void booleanExpression() {
        equalityExpression();
        while (LA(1) == TokenType.AND || LA(1) == TokenType.OR) {
            match(LA(1));
            equalityExpression();
        }
    }

    private void equalityExpression() {
        relationalExpression();
        while (LA(1) == TokenType.OP_EQUALS || LA(1) == TokenType.OP_NOT_EQUALS) { // dla '==' oraz '!='
            match(LA(1));
            relationalExpression();
        }
    }

    private void relationalExpression() {
        additiveExpression();
        while (isRelationalOperator(LA(1))) { // dla '<', '>', '<=', '>='
            match(LA(1));
            additiveExpression();
        }
    }

    private boolean isRelationalOperator(TokenType type) {
        return type == TokenType.OP_SMALLER         // dla '<'
                || type == TokenType.OP_GRATER      // dla '>'
                || type == TokenType.OP_SMALLER_EQUAL  // dla '<='
                || type == TokenType.OP_GRATER_EQUAL; // dla '>='
    }

    private void additiveExpression() {
        multiplicativeExpression();
        while (LA(1) == TokenType.ADD || LA(1) == TokenType.MINUS) { // dla '+' oraz '-'
            match(LA(1));
            multiplicativeExpression();
        }
    }

    private void multiplicativeExpression() {
        primaryExpression();
        while (LA(1) == TokenType.MULT || LA(1) == TokenType.DEVID || LA(1) == TokenType.MODULO) { // dla '*', '/' oraz '%'
            match(LA(1));
            primaryExpression();
        }
    }

    private void primaryExpression() {
        if (LA(1) == TokenType.NAME) {
            if (LA(2) == TokenType.L_BRACK) {
                functionCall();  // jeśli po nazwie następuje nawias otwierający, zakładamy że to wywołanie funkcji
            } else {
                match(TokenType.NAME);
            }
        } else if (LA(1) == TokenType.NUMBER) {
            match(TokenType.NUMBER);
        } else if (LA(1) == TokenType.STRING) {
            match(TokenType.STRING);
        } else if (LA(1) == TokenType.L_BRACK) {
            match(TokenType.L_BRACK);
            expression();
            match(TokenType.R_BRACK);
        } else {
            throw new RuntimeException("expecting primary expression; found " + LT(1));
        }
    }

    private void functionCall() {
        match(TokenType.NAME);
        match(TokenType.L_BRACK);
        if (isExpressionStart(LA(1))) {
            expression();
            while (LA(1) == TokenType.COMMA) {
                match(TokenType.COMMA);
                expression();
            }
        }
        match(TokenType.R_BRACK);
    }

    private void variable() {
    }

    private void functionStatement() {
    }
}

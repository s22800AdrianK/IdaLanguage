package org.example.token;

import java.util.EnumSet;

public enum TokenType {
    WHITESPACE("\\s+"),
    EOF_TYPE("<EOF>"),
    NUMBER("\\d+(\\.\\d+)?"),
    STRING("\"(\\\\.|[^\\\\\"])*\""),
    BOOL("true|false"),
    ADD("\\+"),
    MINUS("\\-"),
    MULT("\\*"),
    DEVID("\\/"),
    MODULO("%"),
    OP_DOT("\\."),
    OP_EQUALS("=="),
    OP_NOT_EQUALS("!="),
    OP_SMALLER_EQUAL("<="),
    OP_GRATER_EQUAL(">="),
    OP_SMALLER("<"),
    OP_GRATER(">"),
    FN("fn"),
    L_BRACK("\\("),
    R_BRACK("\\)"),
    COLON(":"),
    L_C_BRACK("\\{"),
    R_C_BRACK("\\}"),
    L_S_BRACK("\\["),
    R_S_BRACK("\\]"),
    NAME("[a-zA-Z_][a-zA-Z_0-9]*"),
    IF("if"),
    ELSE("else"),
    PRINT("print"),
    WHILE("while"),
    TYPE_NUMBER("num"),
    TYPE_STRING("str"),
    TYPE_BOOL("bool"),
    STRUCT("st"),
    THIS_KEYWORD("this"),
    COMMA(","),
    EQUALS("="),
    OR("\\|\\|"),
    AND("&&"),
    QUOTATION("\"");
    private final String regex;
    TokenType(String regex) {
        this.regex = regex;
    }
    public String getRegex() {
        return regex;
    }
    public static final EnumSet<TokenType> operators = EnumSet.of(
            ADD,
            MINUS,
            MULT,
            DEVID,
            MODULO,
            OP_EQUALS,
            OP_NOT_EQUALS,
            OP_SMALLER,
            OP_GRATER,
            OP_SMALLER_EQUAL,
            OP_GRATER_EQUAL,
            OP_DOT,
            OR,
            AND
    );

    public static final EnumSet<TokenType> nameLikeTokens = EnumSet.of(
            IF,
            ELSE,
            PRINT,
            WHILE,
            TYPE_NUMBER,
            TYPE_STRING,
            TYPE_BOOL,
            STRUCT,
            THIS_KEYWORD
    );
}


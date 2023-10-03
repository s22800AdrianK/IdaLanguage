package org.example.token;

public enum TokenType {
    WHITESPACE("\r|\n| "),
    EOF_TYPE("<EOF>"),
    FLOAT("\\d+\\.\\d+"),
    INT("\\d+"),
    STRING("\"(\\\\.|[^\\\\\"])*\""),
    BOOL("true|false"),
    ADD("\\+"),
    MINUS("\\-"),
    MULT("\\*"),
    DEVID("\\/"),
    MODULO("%"),
    OP_EQUALS("=="),
    OP_NOT_EQUALS("!="),
    OP_SMALLER("<"),
    OP_GRATER(">"),
    OP_SMALLER_EQUAL("<="),
    OP_GRATER_EQUAL(">="),
    FN("fn"),
    L_BRACK("\\("),
    R_BRACK("\\)"),
    COLON(":"),
    L_C_BRACK("\\{"),
    R_C_BRACK("\\}"),
    IF("if"),
    ELSE("else"),
    PRINT("print"),
    TYPE_INT("int"),
    TYPE_FLOAT("float"),
    TYPE_STRING("string"),
    TYPE_BOOL("bool"),
    NAME("[a-zA-Z_][a-zA-Z_0-9]*"),
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
}

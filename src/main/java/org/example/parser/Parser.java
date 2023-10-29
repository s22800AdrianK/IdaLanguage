package org.example.parser;

import org.example.ast.ProgramNode;
import org.example.lexer.Lexer;
import org.example.token.Token;
import org.example.token.TokenType;

public abstract class Parser {
    private final Lexer input;
    private final Token[] lookahead;
    private final int bufferSize;
    private int position;

    public Parser(Lexer lexer, int bufferSize) {
        this.input = lexer;
        this.bufferSize = bufferSize;
        lookahead = new Token[this.bufferSize];
        feedBuffer();
    }

    private void feedBuffer() {
        for (var a : lookahead) {
            consume();
        }
    }

    protected void consume() {
        lookahead[position] = input.nextToken();
        position = (position + 1) % bufferSize;
    }

    protected Token LT(int i) {
        return lookahead[(position + i - 1) % bufferSize];
    }

    protected TokenType LA(int i) {
        return LT(i).getType();
    }

    protected void match(TokenType x) {
        if (LA(1) == x) consume();
        else throw new RuntimeException("expecting " + x + "; found " + LT(1));
    }

    public abstract ProgramNode program();
}

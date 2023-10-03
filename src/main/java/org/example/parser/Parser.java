package org.example.parser;

import org.example.lexer.Lexer;
import org.example.token.Token;

public class Parser {
    Lexer input;
    Token[] lookahead;
    int bufferSize;
    int position = 0;

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
        position = (position+1)%bufferSize;
    }
}

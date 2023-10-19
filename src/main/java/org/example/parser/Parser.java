package org.example.parser;

import org.example.lexer.Lexer;
import org.example.token.Token;
import org.example.token.TokenType;

public abstract class Parser {
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
    public Token LT(int i) {
        return lookahead[(position+i-1)%bufferSize];
    }
    public TokenType LA(int i) {
        return LT(i).getType();
    }

    public void match(TokenType x) {
        System.out.println(x);
        if(LA(1) == x) consume();
        else throw new RuntimeException("expecting "+x+"; found "+LT(1));
    }
}

package org.example;

import org.example.lexer.IdaLexer;
import org.example.lexer.Lexer;
import org.example.token.Token;
import org.example.token.TokenType;

public class Main {
    public static void main(String[] args) {
        String input = "1+2*12";
        Lexer lexer = new IdaLexer(input);
        Token current = lexer.nextToken();
        while (current.getType()!= TokenType.EOF_TYPE) {
            System.out.println(current);
            current = lexer.nextToken();
        }
    }
}
package org.example;

import org.example.lexer.IdaLexer;
import org.example.lexer.Lexer;
import org.example.parser.IdaParser;
import org.example.token.Token;
import org.example.token.TokenType;

public class Main {
    public static void main(String[] args) {
        String input = "1+2*12.12";
        Lexer lexer = new IdaLexer(input);
        IdaParser parser = new IdaParser(lexer,2);
        parser.program();
    }
}
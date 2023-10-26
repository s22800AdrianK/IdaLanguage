package org.example;

import org.example.ast.visitor.SetVarTypesVisitor;
import org.example.lexer.IdaLexer;
import org.example.lexer.Lexer;
import org.example.parser.IdaParser;

public class Main {
    public static void main(String[] args) {
        String input = """
                if 12 == 13 {
                    a:(num>12) = 13
                    c:num = 13
                }
                
                """.trim();
        Lexer lexer = new IdaLexer(input);
        IdaParser parser = new IdaParser(lexer,2);
        var tree = parser.program();
        SetVarTypesVisitor visitor = new SetVarTypesVisitor();
        tree.visit(visitor);
    }
}
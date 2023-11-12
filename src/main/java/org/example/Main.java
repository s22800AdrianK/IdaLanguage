package org.example;

import org.example.ast.visitor.*;
import org.example.interpreter.*;
import org.example.lexer.IdaLexer;
import org.example.lexer.Lexer;
import org.example.parser.IdaParser;
import org.example.scope.SymbolTable;
import org.example.type.TypeResolver;
import org.example.type.TypeResolverImpl;

public class Main {
    public static void main(String[] args) {
        String input = """
                fn fib(a:(num==0)):num { 0 }
                fn fib(a:(num==1)):num { 1 }
                fn fib(a:num):num { fib(a-1) + fib(a-2) }
                print fib(6)
                
                
                """.trim();
        System.out.println();
        Lexer lexer = new IdaLexer(input);
        IdaParser parser = new IdaParser(lexer, 2);
        var tree = parser.program();
        SymbolTable table = new SymbolTable();
        SetVarTypesVisitor visitor = new SetVarTypesVisitorImpl(table);
        tree.visit(visitor);
        SymbolTabVisitorImpl visitor1 = new SymbolTabVisitorImpl(table);
        tree.visit(visitor1);
        TypeResolver resolver = new TypeResolverImpl(table);
        ExpressionTypesVisitor visitor2 = new ExpressionTypesVisitorImpl(resolver,table);
        visitor2.visit(tree);
        IdaInterpreter interpreter = new IdaInterpreterImpl(new MemorySpaceImpl(),new BinaryOperationEvaluator(table), new FunctionCallEvaluator(), table);
        interpreter.execute(tree);
    }
}
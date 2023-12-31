package org.example.pipeline;

import org.example.ast.visitor.ExpressionTypesVisitorImpl;
import org.example.ast.visitor.FuntionValidatiorVisitorImpl;
import org.example.ast.visitor.ValidateASTVisitorImpl;
import org.example.ast.visitor.SymbolTabVisitorImpl;
import org.example.exceptions.handler.ExceptionHandler;
import org.example.handler.Handler;
import org.example.interpreter.*;
import org.example.lexer.IdaLexer;
import org.example.lexer.Lexer;
import org.example.parser.IdaParser;
import org.example.reader.IdaFileReader;
import org.example.reader.IdaFileReaderImpl;
import org.example.scope.SymbolTable;
import org.example.type.TypeResolver;
import org.example.type.TypeResolverImpl;

import java.io.IOException;

public class IdaProcessingPipeline {
    private final String filePath;
    private final SymbolTable symbolTable;
    private final ExpressionEvaluator expressionEvaluator;
    private final MemorySpace memorySpace;
    private final BinaryOperationEvaluator binaryOperationEvaluator;
    private final FunctionCallEvaluator functionCallEvaluator;
    private Handler handlerChain;
    private ExceptionHandler exceptionHandler;
    public IdaProcessingPipeline(String filePath) {
        this.filePath = filePath;
        this.symbolTable = new SymbolTable();
        this.memorySpace = new MemorySpaceImpl();
        this.expressionEvaluator = new ExpressionEvaluator();
        this.binaryOperationEvaluator = new BinaryOperationEvaluator(symbolTable);
        this.functionCallEvaluator = new FunctionCallEvaluator(expressionEvaluator);
        setUpHandlerChain();
    }

    private void setUpHandlerChain() {
        this.exceptionHandler = new ExceptionHandler();
        Handler setVarVisitor = new ValidateASTVisitorImpl();
        Handler symboTabVisitor = new SymbolTabVisitorImpl(symbolTable);
        TypeResolver resolver = new TypeResolverImpl(symbolTable);
        Handler expresionVisitor = new ExpressionTypesVisitorImpl(resolver,symbolTable);
        Handler functionVisitor = new FuntionValidatiorVisitorImpl(symbolTable);
        Handler interpreter =
                new IdaInterpreterImpl(memorySpace,binaryOperationEvaluator,functionCallEvaluator,expressionEvaluator,symbolTable);
        exceptionHandler.setNextHandler(setVarVisitor);
        setVarVisitor.setNextHandler(symboTabVisitor);
        symboTabVisitor.setNextHandler(expresionVisitor);
        expresionVisitor.setNextHandler(functionVisitor);
        functionVisitor.setNextHandler(interpreter);
        this.handlerChain = exceptionHandler;
    }

    public void run() throws IOException {
        IdaFileReader fileReader = new IdaFileReaderImpl();
        String input = fileReader.readFile(filePath);
        this.exceptionHandler.setProgram(input);
        Lexer lexer = new IdaLexer(input);
        IdaParser parser = new IdaParser(lexer, 2);
        this.handlerChain.handle(parser.program());
    }
}

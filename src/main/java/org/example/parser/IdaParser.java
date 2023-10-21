package org.example.parser;

import org.example.ast.*;
import org.example.lexer.Lexer;
import org.example.token.Token;
import org.example.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class IdaParser extends Parser{

    public IdaParser(Lexer lexer, int bufferSize) {
        super(lexer, bufferSize);
    }

    public ProgramNode program() {
        ProgramNode programNode = new ProgramNode();
        while (LA(1) != TokenType.EOF_TYPE){
            programNode.addStatements(statement());
        }
        return programNode;
    }

    private StatementNode statement() {
        return switch (LA(1)) {
            case FN -> functionStatement();
            case NAME -> expresionOrVariable();
            case IF -> ifStatement();
            case PRINT -> printStatement();
            case L_C_BRACK -> block();
            default -> {
                if (isExpressionStart(LA(1))) {
                    yield expressionStatement();
                } else {
                    throw new RuntimeException("expecting statement; found " + LT(1));
                }
            }
        };
    }

    private BlockNode block() {
        BlockNode node = new BlockNode(null);
        match(TokenType.L_C_BRACK);
        while (LA(1)!=TokenType.R_C_BRACK){
            node.addStatement(statement());
        }
        match(TokenType.R_C_BRACK);
        return node;
    }

    private boolean isExpressionStart(TokenType type) {
        return type == TokenType.NAME
                || type == TokenType.NUMBER
                || type == TokenType.STRING
                || type == TokenType.L_BRACK;
    }

    private StatementNode expresionOrVariable() {
        if (LA(2) == TokenType.EQUALS || LA(2) == TokenType.COLON) {
            return variable();
        } else {
            return expressionStatement();
        }
    }
    private StatementNode  printStatement() {
        PrintStatementNode print = new PrintStatementNode(LT(1));
        match(TokenType.PRINT);
        print.setExpression(expression());
        return print;
    }

    private IfStatementNode ifStatement() {
        IfStatementNode ifst = new IfStatementNode(LT(1));
        match(TokenType.IF);
        ifst.setCondition(expression());
        ifst.setThenBlock(block());
        if(LA(1)==TokenType.ELSE) {
            ifst.setElseBlock(block());
        }
        return ifst;
    }

    private StatementNode expressionStatement() {
        return expression();
    }

    private ExpressionNode expression() {
        return booleanExpression();
    }

    private ExpressionNode booleanExpression() {
        ExpressionNode currentExpr = equalityExpression();
        while (LA(1) == TokenType.AND || LA(1) == TokenType.OR) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode right = equalityExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode equalityExpression() {
        ExpressionNode currentExpr = relationalExpression();
        while (LA(1) == TokenType.OP_EQUALS || LA(1) == TokenType.OP_NOT_EQUALS) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode right = relationalExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode relationalExpression() {
        ExpressionNode currentExpr = additiveExpression();
        while (isRelationalOperator(LA(1))) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode right = additiveExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private boolean isRelationalOperator(TokenType type) {
        return type == TokenType.OP_SMALLER         // dla '<'
                || type == TokenType.OP_GRATER      // dla '>'
                || type == TokenType.OP_SMALLER_EQUAL  // dla '<='
                || type == TokenType.OP_GRATER_EQUAL; // dla '>='
    }

    private ExpressionNode additiveExpression() {
        ExpressionNode currentExpr = multiplicativeExpression();
        while (LA(1) == TokenType.ADD || LA(1) == TokenType.MINUS) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode  right = multiplicativeExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode multiplicativeExpression() {
        ExpressionNode currentExpr = primaryExpression();
        while (LA(1) == TokenType.MULT || LA(1) == TokenType.DEVID || LA(1) == TokenType.MODULO) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode right = primaryExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode primaryExpression() {
        ExpressionNode node;
        if (LA(1) == TokenType.NAME) {
            if (LA(2) == TokenType.L_BRACK) {
                node = functionCall();  // jeśli po nazwie następuje nawias otwierający, zakładamy że to wywołanie funkcji
            } else {
                node = new PrimaryExNode(LT(1));
                match(TokenType.NAME);
            }
        } else if (LA(1) == TokenType.NUMBER) {
            node = new PrimaryExNode(LT(1));
            match(TokenType.NUMBER);
        } else if (LA(1) == TokenType.STRING) {
            node = new PrimaryExNode(LT(1));
            match(TokenType.STRING);
        } else if (LA(1) == TokenType.L_BRACK) {
            match(TokenType.L_BRACK);
            node = expression();
            match(TokenType.R_BRACK);
        } else {
            throw new RuntimeException("expecting primary expression; found " + LT(1));
        }
        return node;
    }

    private ExpressionNode functionCall() {
        Token functionName = LT(1);
        match(TokenType.NAME);
        match(TokenType.L_BRACK);
        FunctionCallNode functionCall = new FunctionCallNode(functionName);
        if (isExpressionStart(LA(1))) {
            functionCall.add(expression());
            while (LA(1) == TokenType.COMMA) {
                match(TokenType.COMMA);
                functionCall.add(expression());
            }
        }
        match(TokenType.R_BRACK);
        return functionCall;
    }

    private StatementNode variable() {
        if(LA(2)==TokenType.EQUALS) return assignment();
        else if (LA(2)==TokenType.COLON) return variableDefinition();
        else throw new RuntimeException("expecting typeSpecifier; found "+LT(2));
    }

    private VariableDefNode variableDefinition() {
        Token name = LT(1);
        VariableDefNode node = new VariableDefNode(name);
        node.setVariable(parameter());
        if(LA(1)==TokenType.EQUALS) {
            match(TokenType.EQUALS);
            node.setInitializer(expression());
        }
        return node;
    }

    private TypeSpecifierNode typeSpecifier() {
        TypeSpecifierNode node = new TypeSpecifierNode(LT(1));
         switch (LA(1)) {
            case TYPE_NUMBER -> match(TokenType.TYPE_NUMBER);
            case TYPE_BOOL -> match(TokenType.TYPE_BOOL);
            case TYPE_STRING -> match(TokenType.TYPE_STRING);
            case NAME -> match(TokenType.NAME);
            default -> throw new RuntimeException("expecting typeSpecifier; found "+LT(1));
        }
        return node;
    }

    private AssignmentNode assignment() {
        Token name = LT(1);
        AssignmentNode node = new AssignmentNode(name);
        match(TokenType.NAME);
        match(TokenType.EQUALS);
        node.setExpression(expression());
        return node;
    }
    private FunctionDefNode functionStatement() {
        match(TokenType.FN);
        FunctionDefNode function = new FunctionDefNode(LT(1));
        match(TokenType.NAME);
        match(TokenType.L_BRACK);
        if (LA(1)==TokenType.NAME){
            function.setParameters(parameters());
        }
        match(TokenType.R_BRACK);
        match(TokenType.COLON);
        function.setReturnType(typeSpecifier());
        function.setBody(block());
        return function;
    }

    private List<ParameterNode> parameters() {
        List<ParameterNode> params = new ArrayList<>();
        params.add(parameter());
        while (LA(1)==TokenType.COMMA){
            match(TokenType.COMMA);
            params.add(parameter());
        }
        return params;
    }

    private ParameterNode parameter() {
        Token name = LT(1);
        ParameterNode parameterNode = new ParameterNode(name);
        match(TokenType.NAME);
        match(TokenType.COLON);
        parameterNode.setTypeSpecifier(typeSpecifier());
        return parameterNode;
    }
}

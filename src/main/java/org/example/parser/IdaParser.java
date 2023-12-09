package org.example.parser;

import io.vavr.control.Either;
import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;
import org.example.ast.ArrayAccessNode;
import org.example.lexer.Lexer;
import org.example.token.Token;
import org.example.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class IdaParser extends Parser {

    private enum WorkMode {normal, typeGuard}

    private WorkMode currentMode = WorkMode.normal;

    public IdaParser(Lexer lexer, int bufferSize) {
        super(lexer, bufferSize);
    }

    public ProgramNode program() {
        ProgramNode programNode = new ProgramNode();
        while (LA(1) != TokenType.EOF_TYPE) {
            programNode.addStatements(statement());
        }
        return programNode;
    }

    private StatementNode statement() {
        return switch (LA(1)) {
            case FN -> functionDefinition();
            case NAME -> nameStartStatement();
            case IF -> ifStatement();
            case PRINT -> printStatement();
            case L_C_BRACK -> block();
            case WHILE -> whileStatement();
            case STRUCT -> structDefinition();
            default -> {
                if (isExpressionStart(LA(1))) {
                    yield expressionStatement();
                } else {
                    throw new RuntimeException("expecting statement; found " + LT(1));
                }
            }
        };
    }

    private StatementNode structDefinition() {
        match(TokenType.STRUCT);
        StructureNode node = new StructureNode(LT(1));
        match(TokenType.NAME);
        match(TokenType.L_BRACK);
        node.setConstructorParams(parameters());
        match(TokenType.R_BRACK);
        node.setBody(block());
        return  node;
    }

    private StatementNode whileStatement() {
        WhileStatementNode whileStatementNode = new WhileStatementNode(LT(1));
        match(TokenType.WHILE);
        whileStatementNode.setCondition(expression());
        whileStatementNode.setThenBlock(block());
        return whileStatementNode;
    }

    private BlockNode block() {
        BlockNode node = new BlockNode(null);
        match(TokenType.L_C_BRACK);
        while (LA(1) != TokenType.R_C_BRACK) {
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

    private StatementNode nameStartStatement() {
        if (LA(2) == TokenType.COLON) {
            return variableDefinition();
        }
        return expressionOrAssignmentStatement();
    }

    private StatementNode expressionOrAssignmentStatement() {
        ExpressionNode expr = expression();
        if(LA(1) == TokenType.EQUALS) {
            return assignment(expr);
        }
        return expr;
    }

    private StatementNode printStatement() {
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
        if (LA(1) == TokenType.ELSE) {
            match(TokenType.ELSE);
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
            ExpressionNode right = multiplicativeExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode multiplicativeExpression() {
        ExpressionNode currentExpr = fieldAccessExpression();
        while (LA(1) == TokenType.MULT || LA(1) == TokenType.DEVID || LA(1) == TokenType.MODULO) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode right = fieldAccessExpression();
            BinaryOpNode newExpr = new BinaryOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(right);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode fieldAccessExpression() {
        ExpressionNode currentExpr = primaryExpression();
        while (LA(1) == TokenType.OP_DOT) {
            Token opToken = LT(1);
            match(LA(1));
            ExpressionNode right = primaryExpression();
            Either<FunctionCallNode,Token> either;
            if(right instanceof FunctionCallNode functionCallNode) {
                either = Either.left(functionCallNode);
            }else{
                either = Either.right(right.getToken());
            }
            DotOpNode newExpr = new DotOpNode(opToken);
            newExpr.setLeft(currentExpr);
            newExpr.setRight(either);
            currentExpr = newExpr;
        }
        return currentExpr;
    }

    private ExpressionNode primaryExpression() {
        return switch (currentMode) {
            case normal -> primaryExpressionNormal();
            case typeGuard -> primaryExpressionGuard();
        };
    }

    private ExpressionNode primaryExpressionGuard() {
        ExpressionNode node;
        if (LA(1) == TokenType.NUMBER) {
            node = new PrimaryExNode(LT(1));
            match(TokenType.NUMBER);
        } else if (LA(1) == TokenType.STRING) {
            node = new PrimaryExNode(LT(1));
            match(TokenType.STRING);
        } else if (isTypeIdentifier(LA(1))) {
            node = new PrimaryGuardNode(LT(1));
            match(LA(1));
        } else {
            throw new RuntimeException("expecting primary expression; found " + LT(1));
        }
        return node;
    }

    private ExpressionNode primaryExpressionNormal() {
        ExpressionNode node;
        if (LA(1) == TokenType.NAME) {
            if (LA(2) == TokenType.L_BRACK) {
                node = functionCall();
            } else {
                node = new PrimaryExNode(LT(1));
                match(TokenType.NAME);
            }
        } else if (isNumber(LA(1),LA(2))) {
            node = new PrimaryExNode(handleNumber());
            match(TokenType.NUMBER);
        } else if (LA(1) == TokenType.STRING) {
            node = new PrimaryExNode(LT(1));
            match(TokenType.STRING);
        } else if (LA(1) == TokenType.L_S_BRACK) {
            node = arrayExpr();
        } else if (LA(1) == TokenType.L_BRACK) {
            match(TokenType.L_BRACK);
            node = expression();
            match(TokenType.R_BRACK);
        } else {
            throw new RuntimeException("expecting primary expression; found " + LT(1));
        }

        if(LA(1)==TokenType.L_S_BRACK){
            node = arrayAccess(node);
        }

        return node;
    }

    private ExpressionNode arrayAccess(ExpressionNode node) {
        while (LA(1)==TokenType.L_S_BRACK){
            match(TokenType.L_S_BRACK);
            ExpressionNode index = expression();
            match(TokenType.R_S_BRACK);
            var arr = new ArrayAccessNode(node);
            arr.setIndex(index);
            node = arr;
        }
        return node;
    }

    private ExpressionNode arrayExpr() {
        ArrayNode node = new ArrayNode(LT(1));
        match(TokenType.L_S_BRACK);
        if(LA(1)!=TokenType.R_S_BRACK) {
            node.addExpr(expression());
            while (LA(1)==TokenType.COMMA) {
                match(TokenType.COMMA);
                node.addExpr(expression());
            }
        }
        match(TokenType.R_S_BRACK);
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


    private VariableDefNode variableDefinition() {
        Token name = LT(1);
        VariableDefNode node = new VariableDefNode(name);
        node.setVariable(parameter());
        if (LA(1) == TokenType.EQUALS) {
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
            default -> throw new RuntimeException("expecting typeSpecifier; found " + LT(1));
        }
        while (LA(1)==TokenType.L_S_BRACK) {
            match(TokenType.L_S_BRACK);
            match(TokenType.R_S_BRACK);
            node.incrementarrTypeLevel();
        }
        return node;
    }

    private AssignmentNode assignment(ExpressionNode exprLeft) {
        Token t = LT(1);
        AssignmentNode node = new AssignmentNode(t);
        match(TokenType.EQUALS);
        ExpressionNode right = expression();
        node.setTarget(exprLeft);
        node.setExpression(right);
        return node;
    }

    private FunctionDefNode functionDefinition() {
        match(TokenType.FN);
        FunctionDefNode function = new FunctionDefNode(LT(1));
        match(TokenType.NAME);
        match(TokenType.L_BRACK);
        function.setParameters(parameters());
        match(TokenType.R_BRACK);
        if(LA(1)==TokenType.COLON){
            match(TokenType.COLON);
            function.setReturnType(typeSpecifier());
        }else {
            function.setReturnType(null);
        }

        function.setBody(block());
        return function;
    }

    private List<ParameterNode> parameters() {
        List<ParameterNode> params = new ArrayList<>();
        if(LA(1)==TokenType.NAME){
            params.add(parameter());
        }
        while (LA(1) == TokenType.NAME) {
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
        this.currentMode = WorkMode.typeGuard;
        if (LA(1) == TokenType.L_BRACK) {
            match(TokenType.L_BRACK);
            parameterNode.setGuardExpression(expression());
            match(TokenType.R_BRACK);
        } else {
            parameterNode.setTypeSpecifierNode(typeSpecifier());
        }
        this.currentMode = WorkMode.normal;
        return parameterNode;
    }

    private boolean isTypeIdentifier(TokenType type) {
        return type == TokenType.TYPE_NUMBER
                || type == TokenType.TYPE_BOOL
                || type == TokenType.TYPE_STRING
                || type == TokenType.NAME;
    }

    private boolean isNumber(TokenType first, TokenType second) {
        return first == TokenType.NUMBER ||
                (first == TokenType.MINUS && second == TokenType.NUMBER);
    };

    private Token handleNumber() {
        Token token = LT(1);
        if(LA(1)==TokenType.MINUS) {
            match(TokenType.MINUS);
            token = new Token(TokenType.NUMBER,"-"+LT(1).getValue());
        }
        return token;
    }
}

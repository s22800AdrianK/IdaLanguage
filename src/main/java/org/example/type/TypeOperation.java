package org.example.type;

import org.example.token.TokenType;

import java.util.function.BiFunction;

public class TypeOperation {
    private final Type left;
    private final Type right;
    private final Type resultType;
    private final TokenType operator;
    private final BiFunction<Object,Object,Object> operation;

    public TypeOperation(Type left, Type right, Type resultType, TokenType operator, BiFunction<Object, Object, Object> operation) {
        this.left = left;
        this.right = right;
        this.resultType = resultType;
        this.operator = operator;
        this.operation = operation;
    }

    public Type getLeft() {
        return left;
    }

    public Type getRight() {
        return right;
    }

    public Type getResultType() {
        return resultType;
    }

    public TokenType getOperator() {
        return operator;
    }

    public BiFunction<Object, Object, Object> getOperation() {
        return operation;
    }
}

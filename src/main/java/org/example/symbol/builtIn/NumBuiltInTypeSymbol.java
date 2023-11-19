package org.example.symbol.builtIn;

import org.example.scope.Scope;
import org.example.token.TokenType;
import org.example.type.TypeOperation;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class NumBuiltInTypeSymbol extends BuiltInTypeSymbol{
    public NumBuiltInTypeSymbol(Scope scope) {
        super(TokenType.TYPE_NUMBER.getRegex(),scope);
    }
    public void setupOperations() {
        BiFunction<Object, Object, Object> addOperation = (a, b) -> new BigDecimal(a.toString()).add(new BigDecimal(b.toString()));
        getOperations().add(new TypeOperation(this, this, this,TokenType.ADD, addOperation));

        BiFunction<Object, Object, Object> subtractOperation = (a, b) -> new BigDecimal(a.toString()).subtract(new BigDecimal(b.toString()));
        getOperations().add(new TypeOperation(this, this, this, TokenType.MINUS, subtractOperation));

        BiFunction<Object, Object, Object> multiplyOperation = (a, b) -> new BigDecimal(a.toString()).multiply(new BigDecimal(b.toString()));
        getOperations().add(new TypeOperation(this, this, this, TokenType.MULT, multiplyOperation));

        BiFunction<Object, Object, Object> divideOperation = (a, b) -> new BigDecimal(a.toString()).divide(new BigDecimal(b.toString()));
        getOperations().add(new TypeOperation(this, this, this, TokenType.DEVID, divideOperation));

        BiFunction<Object, Object, Object> moduloOperation = (a, b) -> new BigDecimal(a.toString()).remainder(new BigDecimal(b.toString()));
        getOperations().add(new TypeOperation(this, this, this,TokenType.MODULO, moduloOperation));

        BiFunction<Object, Object, Object> equalsOperation = (a, b) -> new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString())) == 0;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_EQUALS, equalsOperation));

        BiFunction<Object, Object, Object> notEqualsOperation = (a, b) -> new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString())) != 0;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_NOT_EQUALS, notEqualsOperation));

        BiFunction<Object, Object, Object> greaterOperation = (a, b) -> new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString())) > 0;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_GRATER, greaterOperation));

        BiFunction<Object, Object, Object> lessOperation = (a, b) -> new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString())) < 0;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_SMALLER, lessOperation));

        BiFunction<Object, Object, Object> greaterOrEqualOperation = (a, b) -> new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString())) >= 0;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_GRATER_EQUAL, greaterOrEqualOperation));


        BiFunction<Object, Object, Object> lessOrEqualOperation = (a, b) -> new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString())) <= 0;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_SMALLER_EQUAL, lessOrEqualOperation));
    }


}

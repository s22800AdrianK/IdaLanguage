package org.example.symbol.builtIn;

import org.example.scope.Scope;
import org.example.token.TokenType;
import org.example.type.TypeOperation;

import java.util.function.BiFunction;

public class StrBuiltInTypeSymbol extends BuiltInTypeSymbol{
    public StrBuiltInTypeSymbol(Scope scope) {
        super(TokenType.TYPE_STRING.getRegex(), scope);
        setupOperations();
    }

    private void setupOperations() {
        BiFunction<Object, Object, Object> addOperation = (a, b) -> a.toString() + b.toString();
        getOperations().add(new TypeOperation(this, this, this, TokenType.ADD, addOperation));
        getOperations().add(new TypeOperation(this, this.resolveType(TokenType.TYPE_NUMBER.getRegex()), this, TokenType.ADD, addOperation));
        getOperations().add(new TypeOperation(this, this, this, TokenType.ADD, addOperation));

        BiFunction<Object, Object, Object> equalsOperation = Object::equals;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_EQUALS, equalsOperation));

        BiFunction<Object, Object, Object> notEqualsOperation = (a, b) -> !a.equals(b);
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_NOT_EQUALS, notEqualsOperation));
    }


}

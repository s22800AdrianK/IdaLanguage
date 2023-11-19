package org.example.symbol.builtIn;

import org.example.scope.Scope;
import org.example.token.TokenType;
import org.example.type.TypeOperation;

import java.util.function.BiFunction;

public class BoolBuiltInTypeSymbol extends BuiltInTypeSymbol {
    public BoolBuiltInTypeSymbol(Scope scope) {
        super(TokenType.TYPE_BOOL.getRegex(), scope);
    }
    public void setupOperations() {
        BiFunction<Object, Object, Object> andOperation = (a, b) -> (Boolean) a && (Boolean) b;
        getOperations().add(new TypeOperation(this, this,this, TokenType.AND, andOperation));

        BiFunction<Object, Object, Object> orOperation = (a, b) -> (Boolean) a || (Boolean) b;
        getOperations().add(new TypeOperation(this, this, this, TokenType.OR, orOperation));

        BiFunction<Object, Object, Object> equalsOperation = Object::equals;
        getOperations().add(new TypeOperation(this, this, this, TokenType.OP_EQUALS, equalsOperation));

        BiFunction<Object, Object, Object> notEqualsOperation = (a, b) -> !a.equals(b);
        getOperations().add(new TypeOperation(this, this, this, TokenType.OP_NOT_EQUALS, notEqualsOperation));
    }


}

package org.example.symbol.builtIn;

import org.example.helpers.BuiltInFunctionFactory;
import org.example.scope.Scope;
import org.example.symbol.VarSymbol;
import org.example.token.TokenType;
import org.example.type.TypeOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class StrBuiltInTypeSymbol extends BuiltInTypeSymbol{
    public StrBuiltInTypeSymbol(Scope scope) {
        super(TokenType.TYPE_STRING.getRegex(), scope);
    }

    public void setupOperations() {
        BiFunction<Object, Object, Object> addOperation = (a, b) -> a.toString() + b.toString();
        getOperations().add(new TypeOperation(this, this, this, TokenType.ADD, addOperation));
        getOperations().add(new TypeOperation(this, this.resolveType(TokenType.TYPE_NUMBER.getRegex()), this, TokenType.ADD, addOperation));
        getOperations().add(new TypeOperation(this, this, this, TokenType.ADD, addOperation));

        BiFunction<Object, Object, Object> equalsOperation = Object::equals;
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_EQUALS, equalsOperation));

        BiFunction<Object, Object, Object> notEqualsOperation = (a, b) -> !a.equals(b);
        getOperations().add(new TypeOperation(this, this, this.resolveType(TokenType.TYPE_BOOL.getRegex()), TokenType.OP_NOT_EQUALS, notEqualsOperation));
        setupFunctions();
    }

    private void setupFunctions() {
        this.getFunctions().put("charAt", BuiltInFunctionFactory.createBuiltInFunction(
                "charAt",
                this.resolveType(TokenType.TYPE_STRING.getRegex()),
                List.of(new VarSymbol(this.resolveType(TokenType.TYPE_NUMBER.getRegex()),"index")),
                memorySpace -> {
                    int index = ((BigDecimal)memorySpace.getVariable("index")).intValue()-1;
                    return memorySpace.getVariable("this").toString().charAt(index);
                }
        ));
    }


}

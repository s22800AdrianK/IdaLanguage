package org.example.type;

import org.example.scope.SymbolTable;
import org.example.symbol.builtIn.BuiltInTypeSymbol;
import org.example.token.TokenType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeResolverImpl implements TypeResolver{
    private final Map<TokenType, Map<Type, Map<Type, Type>>> map = new HashMap<>();

    public TypeResolverImpl(SymbolTable table) {
        table.getBuiltInTypes().stream()
                .map(BuiltInTypeSymbol::getOperations)
                .flatMap(Collection::stream)
                .forEach(tp -> putEvalType(tp.getOperator(),tp.getLeft(),tp.getRight(),tp.getResultType()));
    }

    public void putEvalType(TokenType opType, Type left, Type right, Type ret){
        validateTokenType(opType);
        map.computeIfAbsent(opType,t->new HashMap<>())
                .computeIfAbsent(left,l->new HashMap<>())
                .put(right,ret);
    }

    @Override
    public Type getEvalType(TokenType opType,Type left, Type right) {
        validateTokenType(opType);
        if (opType == TokenType.OP_DOT) {
            return right; // Return the right-hand type for the dot operator
        }
        return map.getOrDefault(opType,new HashMap<>())
                .getOrDefault(left,new HashMap<>())
                .getOrDefault(right,null);
    }

    public void validateTokenType(TokenType tokenType){
        if(!TokenType.operators.contains(tokenType)) {
            throw new RuntimeException("passed toke is not a operator:"+tokenType);
        }
    }
}

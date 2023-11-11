package org.example.type;

import org.example.scope.SymbolTable;
import org.example.token.TokenType;

import java.util.HashMap;
import java.util.Map;

public class TypeResolverImpl implements TypeResolver{
    private final Map<TokenType, Map<Type, Map<Type, Type>>> map = new HashMap<>();

    public TypeResolverImpl(SymbolTable table) {
        Type num = table.resolveType(TokenType.TYPE_NUMBER.getRegex());
        Type str = table.resolveType(TokenType.TYPE_STRING.getRegex());
        Type bool = table.resolveType(TokenType.TYPE_BOOL.getRegex());

        putEvalType(TokenType.ADD,num,num,num);
        putEvalType(TokenType.ADD,str,str,str);
        putEvalType(TokenType.ADD,num,str,str);
        putEvalType(TokenType.ADD,str,num,str);

        putEvalType(TokenType.MINUS,num,num,num);

        putEvalType(TokenType.MULT,num,num,num);

        putEvalType(TokenType.DEVID,num,num,num);

        putEvalType(TokenType.MODULO,num,num,num);

        putEvalType(TokenType.OP_EQUALS, num, num, bool);
        putEvalType(TokenType.OP_EQUALS, str, str, bool);
        putEvalType(TokenType.OP_EQUALS, bool, bool, bool);

        putEvalType(TokenType.OP_NOT_EQUALS, num, num, bool);
        putEvalType(TokenType.OP_NOT_EQUALS, str, str, bool);
        putEvalType(TokenType.OP_NOT_EQUALS, bool, bool, bool);

        putEvalType(TokenType.OP_SMALLER, num, num, bool);

        putEvalType(TokenType.OP_GRATER, num, num, bool);

        putEvalType(TokenType.OP_SMALLER_EQUAL, num, num, bool);

        putEvalType(TokenType.OP_GRATER_EQUAL, num, num, bool);

        putEvalType(TokenType.OR, bool, bool, bool);

        putEvalType(TokenType.AND,bool,bool,bool);
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

package org.example.type;

import org.example.token.TokenType;

public interface TypeResolver {
    Type getEvalType(TokenType opType,Type left, Type right);

}

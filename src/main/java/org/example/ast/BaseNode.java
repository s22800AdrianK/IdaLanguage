package org.example.ast;

import org.example.token.Token;

public abstract class BaseNode {
    private Token token;

    public BaseNode() {}

    public BaseNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public boolean isAbstractNode() {
        return token==null;
    }
}

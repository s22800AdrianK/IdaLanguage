package org.example.ast;

import org.example.ast.visitor.Visitor;
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
    public abstract void visit(Visitor visitor);
}

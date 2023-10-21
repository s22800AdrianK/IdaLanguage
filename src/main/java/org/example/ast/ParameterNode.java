package org.example.ast;

import org.example.token.Token;

public class ParameterNode extends BaseNode{
    private TypeSpecifierNode typeSpecifier;
    public ParameterNode(Token token) {
        super(token);
    }

    public String getName() {
        return this.getToken().getValue();
    }

    public TypeSpecifierNode getTypeSpecifier() {
        return typeSpecifier;
    }

    public void setTypeSpecifier(TypeSpecifierNode typeSpecifier) {
        this.typeSpecifier = typeSpecifier;
    }
}

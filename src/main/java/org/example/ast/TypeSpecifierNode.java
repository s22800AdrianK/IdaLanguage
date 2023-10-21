package org.example.ast;

import org.example.token.Token;

public class TypeSpecifierNode extends BaseNode {
    public TypeSpecifierNode(Token token) {
        super(token);
    }
    public String getTypeName() {
        return this.getToken().getValue();
    }
}

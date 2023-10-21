package org.example.ast;

import org.example.token.Token;

public class PrimaryExNode extends ExpressionNode{
    public PrimaryExNode(Token token) {
        super(token);
    }

    public String getValue() {
        return this.getToken().getValue();
    }
}

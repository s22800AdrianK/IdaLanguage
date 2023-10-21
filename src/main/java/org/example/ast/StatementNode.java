package org.example.ast;

import org.example.token.Token;

public abstract class StatementNode extends BaseNode {
    public StatementNode(Token token) {
        super(token);
    }
}

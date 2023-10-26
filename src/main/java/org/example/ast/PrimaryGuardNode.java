package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;

public class PrimaryGuardNode extends ExpressionNode{
    public PrimaryGuardNode(Token token) {
        super(token);
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

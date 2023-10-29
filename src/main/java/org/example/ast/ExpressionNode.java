package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;
import org.example.type.Type;

public abstract class ExpressionNode extends StatementNode {
    private Type evalType;

    public ExpressionNode(Token token) {
        super(token);
    }

    public Type getEvalType() {
        return evalType;
    }

    public void setEvalType(Type evalType) {
        this.evalType = evalType;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

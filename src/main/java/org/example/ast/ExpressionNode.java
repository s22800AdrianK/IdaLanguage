package org.example.ast;

import org.example.token.Token;
import org.example.token.TokenType;
import org.example.type.Type;

public abstract class ExpressionNode extends  StatementNode {
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
}

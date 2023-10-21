package org.example.ast;

import org.example.token.Token;
import org.example.token.TokenType;

public abstract class ExpressionNode extends  StatementNode {
    private TokenType evalType;
    public ExpressionNode(Token token) {
        super(token);
    }

    public TokenType getEvalType() {
        return evalType;
    }
    public void setEvalType(TokenType evalType) {
        this.evalType = evalType;
    }
}

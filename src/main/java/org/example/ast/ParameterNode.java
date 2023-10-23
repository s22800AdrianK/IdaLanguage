package org.example.ast;

import org.example.token.Token;
import org.example.type.Type;

import java.util.List;

public class ParameterNode extends BaseNode{
    private List<Type> types;
    private ExpressionNode guardExpression;
    public ParameterNode(Token token) {
        super(token);
    }

    public String getName() {
        return this.getToken().getValue();
    }

    public ExpressionNode getGuardExpression() {
        return guardExpression;
    }

    public void setGuardExpression(ExpressionNode guardExpression) {
        this.guardExpression = guardExpression;
    }
}

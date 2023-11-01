package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;
import org.example.type.Type;

import java.util.List;

public class ParameterNode extends BaseNode {
    private Type type;
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

    public Type getTypes() {
        return type;
    }

    public void setTypes(Type type) {
        this.type = type;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

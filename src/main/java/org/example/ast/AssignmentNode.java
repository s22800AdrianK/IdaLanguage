package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;

public class AssignmentNode extends StatementNode{
    private ExpressionNode expression;
    public AssignmentNode(Token token) {
        super(token);
    }



    public String getVariableName() {
        return this.getToken().getValue();
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(ExpressionNode expression) {
        this.expression = expression;
    }
    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;

public class PrintStatementNode extends StatementNode{
    private ExpressionNode expression;
    public PrintStatementNode(Token token) {
        super(token);
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

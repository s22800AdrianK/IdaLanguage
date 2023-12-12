package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;

public class AssignmentNode extends StatementNode {
    private  ExpressionNode target;
    private  ExpressionNode expression;

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

    public ExpressionNode getTarget() {
        return target;
    }

    public void setTarget(ExpressionNode target) {
        this.target = target;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
    @Override
    public Object execute(IdaInterpreter interpreter) {
        interpreter.execute(this);
        return null;
    }
}

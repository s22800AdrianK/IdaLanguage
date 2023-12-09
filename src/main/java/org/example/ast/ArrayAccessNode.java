package org.example.ast;

import org.example.ast.ExpressionNode;
import org.example.ast.PrimaryExNode;
import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;

public class ArrayAccessNode extends ExpressionNode {

    private ExpressionNode  target;
    private ExpressionNode index;

    public ArrayAccessNode(ExpressionNode node) {
        super(node.getToken());
        this.target = node;
    }
    @Override
    public Object execute(IdaInterpreter interpreter) {
        return interpreter.execute(this);
    }
    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
    public String getName() {
        return this.getToken().getValue();
    }

    public ExpressionNode getIndex() {
        return index;
    }

    public void setIndex(ExpressionNode index) {
        this.index = index;
    }

    public ExpressionNode getTarget() {
        return target;
    }

    public void setTarget(PrimaryExNode target) {
        this.target = target;
    }
}

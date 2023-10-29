package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;

import java.util.ArrayList;
import java.util.List;


public class FunctionCallNode extends ExpressionNode {

    private final List<ExpressionNode> arguments;

    public FunctionCallNode(Token token) {
        super(token);
        this.arguments = new ArrayList<>();
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    public void add(ExpressionNode argument) {
        arguments.add(argument);
    }

    public String getName() {
        return this.getToken().getValue();
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

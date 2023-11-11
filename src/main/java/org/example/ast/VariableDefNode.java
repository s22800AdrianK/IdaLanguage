package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;

import java.util.Optional;

public class VariableDefNode extends StatementNode {
    private ExpressionNode initializer;
    private ParameterNode variable;

    public VariableDefNode(Token token) {
        super(token);
    }

    public Optional<ExpressionNode> getInitializer() {
        return Optional.ofNullable(initializer);
    }

    public void setInitializer(ExpressionNode initializer) {
        this.initializer = initializer;
    }

    public ParameterNode getVariable() {
        return variable;
    }

    public void setVariable(ParameterNode variable) {
        this.variable = variable;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
    @Override
    public Object execute(IdaInterpreter interpreter) {
        return interpreter.execute(this);
    }
}

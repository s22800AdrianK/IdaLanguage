package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;
import org.example.type.Type;

import java.util.Optional;

public class ParameterNode extends BaseNode {
    private Type type;
    private ExpressionNode guardExpression;
    private TypeSpecifierNode typeSpecifierNode;
    public ParameterNode(Token token) {
        super(token);
    }

    public String getName() {
        return this.getToken().getValue();
    }

    public Optional<ExpressionNode> getGuardExpression() {
        return Optional.ofNullable(guardExpression);
    }

    public void setGuardExpression(ExpressionNode guardExpression) {
        this.guardExpression = guardExpression;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TypeSpecifierNode getTypeSpecifierNode() {
        return typeSpecifierNode;
    }

    public void setTypeSpecifierNode(TypeSpecifierNode typeSpecifierNode) {
        this.typeSpecifierNode = typeSpecifierNode;
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
    @Override
    public Object execute(IdaInterpreter interpreter) {
        return null;
    }
}

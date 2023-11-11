package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;

public class PrimaryExNode extends ExpressionNode {
    public PrimaryExNode(Token token) {
        super(token);
    }

    public String getValue() {
        return this.getToken().getValue();
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

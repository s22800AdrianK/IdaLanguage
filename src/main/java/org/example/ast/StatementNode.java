package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;

public abstract class StatementNode extends BaseNode {
    public StatementNode(Token token) {
        super(token);
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

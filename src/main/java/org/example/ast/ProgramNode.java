package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends BaseNode {
    private final List<StatementNode> statements;

    public ProgramNode() {
        super();
        this.statements = new ArrayList<>();
    }

    public List<StatementNode> getStatements() {
        return statements;
    }

    public void addStatements(StatementNode statementNode) {
        this.statements.add(statementNode);
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

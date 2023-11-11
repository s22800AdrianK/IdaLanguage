package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.scope.LocalScope;
import org.example.token.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends StatementNode {
    private final List<StatementNode> statements;
    private LocalScope scope;

    public BlockNode(Token token) {
        super(token);
        this.statements = new ArrayList<>();
    }

    public List<StatementNode> getStatements() {
        return statements;
    }

    public void addStatement(StatementNode statementNode) {
        statements.add(statementNode);
    }

    public LocalScope getScope() {
        return scope;
    }

    public void setScope(LocalScope scope) {
        this.scope = scope;
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

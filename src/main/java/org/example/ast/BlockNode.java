package org.example.ast;

import org.example.token.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends StatementNode{
    private final List<StatementNode> statements;
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
}

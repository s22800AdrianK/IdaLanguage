package org.example.ast;

import org.example.token.Token;

import java.util.Optional;

public class IfStatementNode extends StatementNode{
    private ExpressionNode condition;
    private BlockNode thenBlock;
    private BlockNode elseBlock;

    public IfStatementNode(Token token) {
        super(token);
    }
    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    public BlockNode getThenBlock() {
        return thenBlock;
    }

    public void setThenBlock(BlockNode thenBlock) {
        this.thenBlock = thenBlock;
    }

    public Optional<BlockNode> getElseBlock() {
        return Optional.of(elseBlock);
    }

    public void setElseBlock(BlockNode elseBlock) {
        this.elseBlock = elseBlock;
    }
}
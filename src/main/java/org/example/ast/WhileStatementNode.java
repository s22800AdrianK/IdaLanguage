package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;

public class WhileStatementNode extends StatementNode {
    private ExpressionNode condition;
    private BlockNode thenBlock;
    public WhileStatementNode(Token token) {
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

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object execute(IdaInterpreter interpreter) {
        return interpreter.execute(this);
    }
}

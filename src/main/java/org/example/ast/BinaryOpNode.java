package org.example.ast;

import org.example.ast.ExpressionNode;
import org.example.ast.visitor.Visitor;
import org.example.token.Token;
import org.example.token.TokenType;

public class BinaryOpNode extends ExpressionNode {
    private ExpressionNode left, right;

    public BinaryOpNode(Token addToken) {
        super(addToken);
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public ExpressionNode getRight() {
        return right;
    }

    public TokenType getOperator() {
        return this.getToken().getType();
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
    }
    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

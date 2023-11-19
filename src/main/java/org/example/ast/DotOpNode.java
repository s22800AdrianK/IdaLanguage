package org.example.ast;

import io.vavr.control.Either;
import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.token.Token;
import org.example.token.TokenType;

public class DotOpNode extends ExpressionNode {
    private ExpressionNode left;
    private Either<FunctionCallNode, Token> right;
    public DotOpNode(Token addToken) {
        super(addToken);
    }
    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object execute(IdaInterpreter interpreter) {
        return interpreter.execute(this);
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public Either<FunctionCallNode, Token> getRight() {
        return right;
    }

    public void setRight(Either<FunctionCallNode, Token> right) {
        this.right = right;
    }
}

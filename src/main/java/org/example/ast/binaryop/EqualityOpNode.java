package org.example.ast.binaryop;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;

public class EqualityOpNode extends BinaryOpNode{

    public EqualityOpNode(Token addToken) {
        super(addToken);
    }
    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

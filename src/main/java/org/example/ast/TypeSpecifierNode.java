package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;

public class TypeSpecifierNode extends BaseNode {
    public TypeSpecifierNode(Token token) {
        super(token);
    }

    public String getTypeName() {
        return this.getToken().getValue();
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
}

package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.token.Token;
import org.example.type.Type;

public class TypeSpecifierNode extends BaseNode {
    private Type type;
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
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

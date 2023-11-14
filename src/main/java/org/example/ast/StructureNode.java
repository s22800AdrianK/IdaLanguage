package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.symbol.StructureSymbol;
import org.example.token.Token;

public class StructureNode extends StatementNode{
    private BlockNode body;

    private StructureSymbol symbol;
    public StructureNode(Token token) {
        super(token);
    }

    public String getName() {
        return this.getToken().getValue();
    }

    public BlockNode getBody() {
        return body;
    }

    public void setBody(BlockNode body) {
        this.body = body;
    }

    @Override
    public void visit(Visitor visitor) {

    }
    @Override
    public Object execute(IdaInterpreter interpreter) {
        return null;
    }

    public StructureSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(StructureSymbol symbol) {
        this.symbol = symbol;
    }
}

package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.symbol.builtIn.ArrayBuiltInTypeSymbol;
import org.example.token.Token;

import java.util.ArrayList;
import java.util.List;

public class ArrayNode extends ExpressionNode{
    List<ExpressionNode> elements = new ArrayList<>();
    ArrayBuiltInTypeSymbol symbol;
    public ArrayNode(Token token) {
        super(token);
    }

    public List<ExpressionNode> getElements() {
        return elements;
    }

    public void setElements(List<ExpressionNode> elements) {
        this.elements = elements;
    }

    public void addExpr(ExpressionNode node) {
        elements.add(node);
    }

    public ArrayBuiltInTypeSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(ArrayBuiltInTypeSymbol symbol) {
        this.symbol = symbol;
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

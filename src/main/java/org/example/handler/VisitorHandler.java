package org.example.handler;

import org.example.ast.ProgramNode;
import org.example.ast.visitor.Visitor;

public abstract class VisitorHandler implements Handler , Visitor {
    private Handler next;

    @Override
    public void setNextHandler(Handler handler) {
        this.next = handler;
    }
    @Override
    public void handle(ProgramNode programNode) {
        programNode.visit(this);
        if(next!=null)
            next.handle(programNode);
    }
}

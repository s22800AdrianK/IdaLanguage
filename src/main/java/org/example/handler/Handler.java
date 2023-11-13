package org.example.handler;

import org.example.ast.ProgramNode;

public interface Handler {
    void setNextHandler(Handler handler);

    void handle(ProgramNode programNode);
}

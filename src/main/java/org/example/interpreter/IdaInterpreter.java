package org.example.interpreter;

import org.example.ast.*;
import org.example.ast.PrimaryExNode;
import org.example.ast.ArrayAccessNode;

public interface IdaInterpreter {
    void execute(ProgramNode node);
    void execute(AssignmentNode node);
    Object execute(BlockNode node);
    void execute(IfStatementNode node);
    void execute(PrintStatementNode node);
    Object execute(ExpressionNode expressionNode);
    Object execute(FunctionCallNode node);
    Object execute(BinaryOpNode node);
    Object execute(PrimaryExNode node);
    Object execute(PrimaryGuardNode node);
    void execute(VariableDefNode node);
    void execute(WhileStatementNode node);
    Object execute(DotOpNode node);
    Object execute(ArrayAccessNode node);
    ArrayInstance execute(ArrayNode node);
}

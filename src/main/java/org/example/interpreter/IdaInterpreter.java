package org.example.interpreter;

import org.example.ast.*;
import org.example.ast.PrimaryExNode;

public interface IdaInterpreter {
    Object execute(ProgramNode node);
    Object execute(AssignmentNode node);
    Object execute(BlockNode node);
    Object execute(IfStatementNode node);
    Object execute(PrintStatementNode node);
    Object execute(ExpressionNode expressionNode);
    Object execute(FunctionCallNode node);
    Object execute(BinaryOpNode node);
    Object execute(PrimaryExNode node);
    Object execute(PrimaryGuardNode node);
    Object execute(FunctionDefNode node);
    Object execute(ParameterNode node);
    Object execute(TypeSpecifierNode node);
    Object execute(VariableDefNode node);
    Object execute(StatementNode node);
}

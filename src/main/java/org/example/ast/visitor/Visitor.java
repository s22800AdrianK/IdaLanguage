package org.example.ast.visitor;

import org.example.ast.*;

public interface Visitor {
    void visit(BaseNode node);
    void visit(AssignmentNode node);
    void visit(BinaryOpNode node);
    void visit(BlockNode node);
    void visit(ExpressionNode node);
    void visit(FunctionCallNode node);
    void visit(FunctionDefNode node);
    void visit(IfStatementNode node);
    void visit(ParameterNode node);
    void visit(PrimaryExNode node);
    void visit(PrintStatementNode node);
    void visit(ProgramNode node);
    void visit(TypeSpecifierNode node);
    void visit(VariableDefNode node);
    void visit(StatementNode node);
}

package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;

public interface SymbolTableVisitor extends Visitor{
    void visit(AssignmentNode node);

    void visit(BinaryOpNode node);

    void visit(BlockNode node);

    default void visit(ExpressionNode node){}

    void visit(FunctionCallNode node);

    void visit(FunctionDefNode node);

    void visit(IfStatementNode node);

    default void visit(ParameterNode node){}

    void visit(PrimaryExNode node);

    void visit(PrintStatementNode node);

    void visit(ProgramNode node);

    void visit(TypeSpecifierNode node);

    void visit(VariableDefNode node);

    default void visit(StatementNode node){}

    default void visit(PrimaryGuardNode node){}
}

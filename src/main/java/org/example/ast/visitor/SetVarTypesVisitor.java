package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.primaryex.PrimaryExNode;

public interface SetVarTypesVisitor extends Visitor {
    default void visit(AssignmentNode node) {
    }

    void visit(BinaryOpNode node);

    void visit(BlockNode node);

    default void visit(ExpressionNode node) {
    }

    default void visit(FunctionCallNode node) {
    }

    void visit(FunctionDefNode node);

    void visit(IfStatementNode node);

    void visit(ParameterNode node);

    default void visit(PrimaryExNode node) {
    }

    default void visit(PrintStatementNode node) {
    }

    void visit(ProgramNode node);

    default void visit(TypeSpecifierNode node) {
    }

    void visit(VariableDefNode node);

    default void visit(StatementNode node) {
    }

    void visit(PrimaryGuardNode node);
}

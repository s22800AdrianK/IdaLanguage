package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;

public interface ValidateASTVisitor extends Visitor {
    default void visit(AssignmentNode node) {
    }

    default void visit(ExpressionNode node) {
    }

    default void visit(FunctionCallNode node) {}

    default void visit(PrimaryExNode node) {
    }

    default void visit(PrintStatementNode node) {
    }

    default void visit(TypeSpecifierNode node) {
    }

    default void visit(StatementNode node) {
    }
}

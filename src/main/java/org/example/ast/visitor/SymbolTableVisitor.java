package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;

public interface SymbolTableVisitor extends Visitor{
    default void visit(ExpressionNode node){}

    default void visit(ParameterNode node){}

    default  void visit(TypeSpecifierNode node){};

    default void visit(StatementNode node){}

    default void visit(PrimaryGuardNode node){}
}

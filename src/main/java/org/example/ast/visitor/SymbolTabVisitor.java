package org.example.ast.visitor;

import org.example.ast.*;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;

import java.util.List;

public class SymbolTabVisitor implements Visitor{
    private Scope currentScope = new SymbolTable();

    @Override
    public void visit(AssignmentNode node) {

    }

    @Override
    public void visit(BinaryOpNode node) {
            node.getLeft().visit(this);
            node.getRight().visit(this);
    }

    @Override
    public void visit(BlockNode node) {

    }

    @Override
    public void visit(ExpressionNode node) {

    }

    @Override
    public void visit(FunctionCallNode node) {

    }

    @Override
    public void visit(FunctionDefNode node) {

    }

    @Override
    public void visit(IfStatementNode node) {

    }

    @Override
    public void visit(ParameterNode node) {

    }

    @Override
    public void visit(PrimaryExNode node) {

    }

    @Override
    public void visit(PrintStatementNode node) {

    }

    @Override
    public void visit(ProgramNode node) {
            node.getStatements().forEach(n->n.visit(this));
    }

    @Override
    public void visit(TypeSpecifierNode node) {

    }

    @Override
    public void visit(VariableDefNode node) {
            //currentScope.defineSymbol(node.getVariable().getName(),node.getVariable().getTypes());
    }

    @Override
    public void visit(StatementNode node) {

    }

    @Override
    public void visit(PrimaryGuardNode node) {

    }
}

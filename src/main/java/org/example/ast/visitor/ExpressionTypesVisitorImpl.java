package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.binaryop.BinaryOpNode;
import org.example.ast.primaryex.PrimaryExNode;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;

import java.util.List;

public class ExpressionTypesVisitorImpl implements ExpressionTypesVisitor{
    private Scope currentScope;
    public ExpressionTypesVisitorImpl(SymbolTable symbolTable) {
        currentScope = symbolTable;
    }
    @Override
    public void visit(AssignmentNode node) {

    }

    @Override
    public void visit(BinaryOpNode node) {
        ExpressionNode left  = node.getLeft();
        ExpressionNode right = node.getRight();
        left.visit(this);
        right.visit(this);
            switch (node.getOperator()) {
                case ADD -> setAddOperatorReturnType(left,right);
            }
    }

    private void setAddOperatorReturnType(ExpressionNode left, ExpressionNode right) {

    }

    @Override
    public void visit(BlockNode node) {
        node.getStatements().forEach(e->e.visit(this));
    }

    @Override
    public void visit(ExpressionNode node) {

    }

    @Override
    public void visit(FunctionCallNode node) {
            node.setEvalType(currentScope.resolveType(node.getName()));
    }

    @Override
    public void visit(FunctionDefNode node) {
        currentScope = node.getFunctionSymbol();

        if (node.getReturnType() != null) {
            node.getReturnType().visit(this);
        }

        node.getBody().visit(this);

        if (hasWrongReturnDeclaration(node)) {
            throw new RuntimeException("Wrong return declaration");
        }

        currentScope = currentScope.getUpperScope();
    }

    private boolean hasWrongReturnDeclaration(FunctionDefNode node) {
        if (node.getReturnType() != null) {
            ExpressionNode lastStatement = getLastStatement(node.getBody());
            if (lastStatement != null) {
                return !lastStatement.getEvalType().equals(node.getReturnType().getType());
            }
        }
        return false;
    }

    private ExpressionNode getLastStatement(BlockNode blockNode) {
        List<StatementNode> statements = blockNode.getStatements();
        if (!statements.isEmpty() && statements.get(statements.size() - 1) instanceof ExpressionNode) {
            return (ExpressionNode) statements.get(statements.size() - 1);
        }
        return null;
    }

    @Override
    public void visit(IfStatementNode node) {

    }

    @Override
    public void visit(ParameterNode node) {

    }
    @Override
    public void visit(PrimaryExNode node) {
        switch (node.getToken().getType()) {
            case NUMBER -> node.setEvalType(currentScope.resolveType("num"));
            case STRING -> node.setEvalType(currentScope.resolveType("string"));
            case BOOL -> node.setEvalType(currentScope.resolveType("bool"));
            case NAME -> node.setEvalType(currentScope.resolve(node.getValue()).getType());
        }
    }

    @Override
    public void visit(PrintStatementNode node) {

    }

    @Override
    public void visit(ProgramNode node) {
        node.getStatements().forEach(e->e.visit(this));
    }
    @Override
    public void visit(TypeSpecifierNode node) {
        node.setType(currentScope.resolveType(node.getTypeName()));
    }

    @Override
    public void visit(VariableDefNode node) {

    }

    @Override
    public void visit(StatementNode node) {

    }

    @Override
    public void visit(PrimaryGuardNode node) {

    }
}

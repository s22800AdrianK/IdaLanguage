package org.example.ast.visitor;

import org.example.ast.*;
import org.example.exceptions.ArgumentTypeMismatch;
import org.example.exceptions.ImplementationArgumentNumberException;
import org.example.handler.VisitorHandler;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;
import org.example.symbol.FunctionAggregateSymbol;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FuntionValidatiorVisitorImpl extends VisitorHandler implements FuntionValidatorVisitor{
    private Scope currentScope;
    public FuntionValidatiorVisitorImpl(SymbolTable symbolTable) {
        currentScope = symbolTable;
    }
    @Override
    public void visit(AssignmentNode node) {

    }

    @Override
    public void visit(BinaryOpNode node) {

    }

    @Override
    public void visit(BlockNode node) {
        currentScope = node.getScope();
        node.getStatements().forEach(e -> e.visit(this));
        currentScope = node.getScope().getUpperScope();
    }

    @Override
    public void visit(ExpressionNode node) {

    }

    @Override
    public void visit(FunctionCallNode node) {

    }

    @Override
    public void visit(FunctionDefNode node) {
        var implementations = (FunctionAggregateSymbol)currentScope.resolve(node.getToken().getValue());

        Set<Integer> argumentCounts = implementations.getFunctionSymbols().stream()
                .map(fs -> fs.getSymbols().size())
                .collect(Collectors.toSet());

        if (argumentCounts.size() != 1) {
            throw new ImplementationArgumentNumberException(node.getToken().getValue(),node.getToken().getLine());
        }

        boolean typesMatch = IntStream.range(0, argumentCounts.iterator().next())
                .allMatch(i -> implementations.getFunctionSymbols().stream()
                        .map(fs -> fs.getSymbols().get(i).getType())
                        .distinct()
                        .limit(2)
                        .count() <= 1);

        if (!typesMatch) {
            throw new ArgumentTypeMismatch(node.getToken().getValue(),node.getToken().getLine());
        }
    }

    @Override
    public void visit(IfStatementNode node) {
        node.getThenBlock().visit(this);
        node.getElseBlock().ifPresent(e->e.visit(this));
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
        node.getStatements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(TypeSpecifierNode node) {

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

    @Override
    public void visit(WhileStatementNode node) {
        node.getThenBlock().visit(this);
    }

    @Override
    public void visit(StructureNode node) {
        currentScope = node.getSymbol();
        node.getBody().visit(this);
        currentScope = node.getSymbol().getUpperScope();
    }

    @Override
    public void visit(DotOpNode node) {

    }

    @Override
    public void visit(ArrayNode node) {

    }

    @Override
    public void visit(ArrayAccessNode node) {

    }
}

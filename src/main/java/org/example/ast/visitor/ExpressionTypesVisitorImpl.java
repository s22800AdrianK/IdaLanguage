package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.primaryex.PrimaryExNode;
import org.example.exceptions.ArgumentTypeMismatch;
import org.example.exceptions.ImplementationArgumentNumberException;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.type.Type;
import org.example.type.TypeResolver;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpressionTypesVisitorImpl implements ExpressionTypesVisitor {

    private final TypeResolver typeResolver;
    private Scope currentScope;

    public ExpressionTypesVisitorImpl(TypeResolver typeResolver, SymbolTable symbolTable) {
        this.typeResolver = typeResolver;
        currentScope = symbolTable;
    }

    @Override
    public void visit(AssignmentNode node) {

    }

    @Override
    public void visit(BinaryOpNode node) {
        ExpressionNode left = node.getLeft();
        ExpressionNode right = node.getRight();
        left.visit(this);
        right.visit(this);
        Type ret = typeResolver.getEvalType(node.getOperator(), left.getEvalType(), right.getEvalType());
        if (ret == null) {
            throw new RuntimeException(
                    "operator:" + node.getOperator() + "not allowed for:" + left.getEvalType() + " and " + right.getEvalType()
            );
        }
        node.setEvalType(ret);
    }

    @Override
    public void visit(BlockNode node) {
        currentScope = node.getScope();
        node.getStatements().forEach(e -> e.visit(this));
        currentScope = currentScope.getUpperScope();
    }

    @Override
    public void visit(ExpressionNode node) {

    }

    @Override
    public void visit(FunctionCallNode node) {
        node.getArguments().forEach(e->e.visit(this));

        FunctionSymbol fn = (FunctionSymbol)currentScope.resolve(node.getName());
        List<Symbol> argList= fn.getImplementations().keySet().stream().toList().get(0);
        boolean typesMatch = IntStream.range(0,argList.size())
                        .allMatch(i->argList.get(i).getType().equals(node.getArguments().get(i).getEvalType()));

        if(typesMatch) {
            throw new RuntimeException();
        }

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

        var implementations = node.getFunctionSymbol().getImplementations();

        Set<Integer> sizes = implementations.keySet().stream()
                .map(List::size)
                .collect(Collectors.toSet());

        if(sizes.size()!=1) {
            throw new ImplementationArgumentNumberException(node.getToken().getValue());
        }

        boolean typesMatch = IntStream.range(0,sizes.size())
                .allMatch(i-> implementations.keySet()
                                    .stream().map(list->list.get(i).getType())
                                    .distinct()
                                    .limit(2)
                                    .count()==1
                );

        if(typesMatch) {
            throw new ArgumentTypeMismatch(node.getToken().getValue());
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
    public void visit(PrintStatementNode node) {}

    @Override
    public void visit(ProgramNode node) {
        node.getStatements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(TypeSpecifierNode node) {
        node.setType(currentScope.resolveType(node.getTypeName()));
    }

    @Override
    public void visit(VariableDefNode node) {
            node.getInitializer().ifPresent(e->{
                e.visit(this);
                if(!node.getVariable().getTypes().equals(e.getEvalType())){
                    throw new RuntimeException("type: "+e.getEvalType()+" can't be assigned to "+node.getVariable().getTypes());
                }
            });
    }

    @Override
    public void visit(StatementNode node) {}

    @Override
    public void visit(PrimaryGuardNode node) {
        node.setEvalType(currentScope.resolveType(node.getToken().getValue()));
    }
}

package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.exceptions.ToManyTypesInGuardException;
import org.example.scope.SymbolTable;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class SetVarTypesVisitorImpl implements SetVarTypesVisitor {
    private final List<PrimaryGuardNode> types = new ArrayList<>();
    private final SymbolTable currentScope;

    public SetVarTypesVisitorImpl(SymbolTable currentScope) {
        this.currentScope = currentScope;
    }

    @Override
    public void visit(BinaryOpNode node) {
        node.getLeft().visit(this);
        node.getRight().visit(this);

    }

    @Override
    public void visit(BlockNode node) {
        node.getStatements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(FunctionDefNode node) {
        node.getParameters().forEach(e->e.visit(this));
        node.getBody().visit(this);
    }

    @Override
    public void visit(IfStatementNode node) {
        node.getThenBlock().visit(this);
        node.getElseBlock().ifPresent(this::visit);
    }

    @Override
    public void visit(ParameterNode node) {
        if(node.getGuardExpression().isEmpty()) {
            node.setTypes(currentScope.resolveType(node.getTypeSpecifierNode().getTypeName()));
            return;
        }
        node.getGuardExpression().get().visit(this);
        List<Type> a = this.types.stream()
                .map(e -> currentScope.getBuliInTypeForName(e.getToken().getValue()))
                .distinct()
                .toList();
        if(a.size()>1){
            throw new ToManyTypesInGuardException(node.getName());
        }
        node.setTypes(a.get(0));
        types.clear();
    }

    @Override
    public void visit(ProgramNode node) {
        node.getStatements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(VariableDefNode node) {
        node.getVariable().visit(this);
    }


    @Override
    public void visit(PrimaryGuardNode node) {
        types.add(node);
    }
}

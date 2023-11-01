package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.binaryop.BinaryOpNode;
import org.example.scope.SymbolTable;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class SetVarTypesVisitorImpl implements SetVarTypesVisitor {
    private List<PrimaryGuardNode> types = new ArrayList<>();
    private final SymbolTable currentScope = new SymbolTable();

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
        node.getBody().visit(this);
    }

    @Override
    public void visit(IfStatementNode node) {
        node.getThenBlock().visit(this);
        node.getElseBlock().ifPresent(this::visit);
    }

    @Override
    public void visit(ParameterNode node) {
        node.getGuardExpression().visit(this);
        List<Type> a = this.types.stream()
                .map(e -> currentScope.getBuliInTypeForName(e.getToken().getValue()))
                .distinct()
                .toList();
        if(a.size()>1){
            throw new RuntimeException();
        }
        node.setTypes(a.get(0));
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

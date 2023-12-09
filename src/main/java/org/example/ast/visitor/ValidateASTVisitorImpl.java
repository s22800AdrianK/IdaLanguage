package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.exceptions.NonlegalStatementInStruct;
import org.example.handler.VisitorHandler;
import org.example.exceptions.ToManyTypesInGuardException;

import java.util.ArrayList;
import java.util.List;

public class ValidateASTVisitorImpl extends VisitorHandler implements ValidateASTVisitor {
    private final List<PrimaryGuardNode> types = new ArrayList<>();

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
            return;
        }
        node.getGuardExpression().get().visit(this);
        if(types.size()>1){
            throw new ToManyTypesInGuardException(node.getName());
        }
        node.setTypeSpecifierNode(new TypeSpecifierNode(types.get(0).getToken()));
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

    @Override
    public void visit(WhileStatementNode node) {
        node.getThenBlock().visit(this);
    }

    @Override
    public void visit(StructureNode node) {
        node.getBody().getStatements()
                .stream()
                .filter(st->!st.getClass().equals(VariableDefNode.class) && !st.getClass().equals(FunctionDefNode.class))
                .findFirst()
                .ifPresent(e->{throw new NonlegalStatementInStruct(node.getToken().getValue());});
        node.getConstructorParams().forEach(e->e.visit(this));
        node.getBody().visit(this);
    }

    @Override
    public void visit(DotOpNode node) {
        node.getLeft().visit(this);
        node.getRight().peekLeft(f->f.visit(this));
    }

    @Override
    public void visit(ArrayNode node) {
        node.getElements().forEach(e->e.visit(this));
    }

    @Override
    public void visit(ArrayAccessNode node) {
        node.getTarget().visit(this);
        node.getIndex().visit(this);
    }
}

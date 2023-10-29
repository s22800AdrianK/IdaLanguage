package org.example.ast.visitor;

import org.example.ast.*;
import org.example.scope.LocalScope;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;
import org.example.token.TokenType;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class SymbolTabVisitorImpl implements Visitor {
    private Scope currentScope = new SymbolTable();

    @Override
    public void visit(AssignmentNode node) {
        node.getExpression().visit(this);
    }

    @Override
    public void visit(BinaryOpNode node) {
        node.getLeft().visit(this);
        node.getRight().visit(this);
    }

    @Override
    public void visit(BlockNode node) {
        currentScope = new LocalScope(this.currentScope);
        node.getStatements().forEach(e->e.visit(this));
        currentScope = currentScope.getUpperScope();
    }

    @Override
    public void visit(ExpressionNode node) {}

    @Override
    public void visit(FunctionCallNode node) {
            if(!currentScope.checkIfAlreadyDefined(node.getName())){
                throw new RuntimeException();
            }
    }

    @Override
    public void visit(FunctionDefNode node) {
        Type retType = currentScope.resolveType(node.getReturnType().getTypeName());
        Symbol func = new FunctionSymbol(node.getToken().getValue(), List.of(retType),currentScope);
        currentScope.defineSymbol(func);
        currentScope = (Scope)func;
        node.getParameters().forEach(p->currentScope.defineSymbol(new Symbol(p.getName(),p.getTypes())));
        node.getBody().visit(this);
        currentScope = currentScope.getUpperScope();
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
        if(node.getToken().getType()== TokenType.NAME && !currentScope.checkIfAlreadyDefined(node.getValue())){
            throw new RuntimeException("variable not defined");
        }
    }

    @Override
    public void visit(PrintStatementNode node) {
            node.getExpression().visit(this);
    }

    @Override
    public void visit(ProgramNode node) {
        node.getStatements().forEach(n -> n.visit(this));
    }

    @Override
    public void visit(TypeSpecifierNode node) {
        if(node.getToken().getType()== TokenType.NAME && !currentScope.checkIfAlreadyDefined(node.getTypeName())){
            throw new RuntimeException("type not defined");
        }
    }

    @Override
    public void visit(VariableDefNode node) {
        Symbol var = new VarSymbol(node.getVariable().getName(),node.getVariable().getTypes());
        if(currentScope.checkIfAlreadyDefined(var.getName())){
            throw new RuntimeException("VARIABLE ALREADY DEFINED");
        }
        currentScope.defineSymbol(var);
        node.getInitializer().ifPresent(e->e.visit(this));
    }

    @Override
    public void visit(StatementNode node) {

    }

    @Override
    public void visit(PrimaryGuardNode node) {

    }
}

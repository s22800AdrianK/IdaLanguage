package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;
import org.example.handler.VisitorHandler;
import org.example.exceptions.TypeNotDefinedException;
import org.example.exceptions.VariableAlreadyDefinedException;
import org.example.exceptions.VariableNotDefinedException;
import org.example.scope.LocalScope;
import org.example.scope.Scope;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.StructureSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;
import org.example.token.TokenType;

import java.util.List;

public class SymbolTabVisitorImpl extends VisitorHandler implements SymbolTableVisitor {
    private Scope currentScope;

    public SymbolTabVisitorImpl(Scope currentScope) {
        this.currentScope = currentScope;
    }

    @Override
    public void visit(AssignmentNode node) {
        node.getTarget().visit(this);
        node.getExpression().visit(this);
    }

    @Override
    public void visit(BinaryOpNode node) {
        node.getLeft().visit(this);
        node.getRight().visit(this);
    }

    @Override
    public void visit(BlockNode node) {
        LocalScope newScope = new LocalScope(this.currentScope);
        currentScope = newScope;
        node.setScope(newScope);
        node.getStatements().forEach(e->e.visit(this));
        currentScope = currentScope.getUpperScope();
    }

    @Override
    public void visit(FunctionCallNode node) {}

    @Override
    public void visit(FunctionDefNode node) {
        List<Symbol> args = node.getParameters().stream().map(e->(Symbol)new VarSymbol(e.getName(),e.getGuardExpression().orElse(null))).toList();
        FunctionSymbol func;
        Symbol symbol = currentScope.resolve(node.getToken().getValue());
        if((symbol instanceof FunctionSymbol existing)){
            if(existing.getSpecifierNode().getTypeName().equals(node.getReturnType().map(TypeSpecifierNode::getTypeName).orElse(null))){
                throw new RuntimeException("Function with the same name already exists but with a different return type");
            }
            func = existing;
            func.addNewImplementation(args,node.getBody());
        }else {
            func = new FunctionSymbol(node.getToken().getValue(),args,node.getReturnType().orElse(null),node.getBody(),currentScope);
        }
        currentScope.defineSymbol(func);
        currentScope = func;
        node.setFunctionSymbol(func);
        node.getBody().visit(this);
        currentScope = currentScope.getUpperScope();
    }

    @Override
    public void visit(IfStatementNode node) {
            node.getThenBlock().visit(this);
            node.getElseBlock().ifPresent(e->e.visit(this));
    }

    @Override
    public void visit(PrimaryExNode node) {
        if(node.getToken().getType()==TokenType.NAME && !currentScope.checkIfAlreadyDefined(node.getValue())){
            throw new VariableNotDefinedException(node.getValue());
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
            throw new TypeNotDefinedException(node.getTypeName());
        }
    }

    @Override
    public void visit(VariableDefNode node) {
        Symbol var = new VarSymbol(node.getVariable().getName(), node.getVariable().getGuardExpression().orElse(null));
        if(currentScope.checkIfAlreadyDefined(var.getName())){
            throw new VariableAlreadyDefinedException(node.getVariable().getName());
        }
        currentScope.defineSymbol(var);
        node.getInitializer().ifPresent(e->e.visit(this));
    }
    @Override
    public void visit(WhileStatementNode node) {
        node.getThenBlock().visit(this);
    }

    @Override
    public void visit(StructureNode node) {
        if(currentScope.checkIfAlreadyDefined(node.getName())) {
            throw new VariableAlreadyDefinedException(node.getName());
        }
        List<Symbol> args = node.getConstructorParams().stream().map(e->(Symbol)new VarSymbol(e.getName(),e.getGuardExpression().orElse(null))).toList();
        StructureSymbol struct = new StructureSymbol(args, node.getName(),currentScope, node.getBody());
        currentScope.defineSymbol(struct);
        currentScope = struct;
        node.getBody().visit(this);
        node.getBody().getScope().getSymbols().forEach(symbol -> {
            if(symbol instanceof FunctionSymbol functionSymbol) {
                struct.getFunctions().put(functionSymbol.getName(),functionSymbol);
            }else {
                struct.getFields().put(symbol.getName(),symbol);
            }
        });
        node.setSymbol(struct);
        currentScope = currentScope.getUpperScope();
    }

    @Override
    public void visit(DotOpNode node) {
        node.getLeft().visit(this);
        node.getRight().peekLeft(f->f.visit(this));
    }

}

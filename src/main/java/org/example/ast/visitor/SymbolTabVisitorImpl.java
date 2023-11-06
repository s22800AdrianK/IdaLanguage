package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.primaryex.PrimaryExNode;
import org.example.exceptions.NotAFunctionException;
import org.example.exceptions.TypeNotDefinedException;
import org.example.exceptions.VariableAlreadyDefinedException;
import org.example.exceptions.VariableNotDefinedException;
import org.example.scope.LocalScope;
import org.example.scope.Scope;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;
import org.example.token.TokenType;
import org.example.type.Type;

import java.util.List;

public class SymbolTabVisitorImpl implements SymbolTableVisitor {
    private Scope currentScope;

    public SymbolTabVisitorImpl(Scope currentScope) {
        this.currentScope = currentScope;
    }

    @Override
    public void visit(AssignmentNode node) {
        if(!currentScope.checkIfAlreadyDefined(node.getVariableName())){
            throw new VariableNotDefinedException(node.getVariableName());
        }
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
    public void visit(FunctionCallNode node) {
            if(!(currentScope.resolve(node.getName()) instanceof FunctionSymbol)){
                throw new NotAFunctionException(node.getName());
            }
    }

    @Override
    public void visit(FunctionDefNode node) {
        Type retType = currentScope.resolveType(node.getReturnType().getTypeName());
        List<Symbol> args = node.getParameters().stream().map(e->new Symbol(e.getName(),e.getTypes())).toList();
        FunctionSymbol func;
        Symbol symbol = currentScope.resolve(node.getToken().getValue());
        if((symbol instanceof FunctionSymbol existing)){
            if(existing.getType().equals(retType)){
                throw new RuntimeException("Function with the same name already exists but with a different return type");
            }
            func = existing;
            func.addNewImplementation(args,node.getBody());
        }else {
            func = new FunctionSymbol(node.getToken().getValue(),retType,args,node.getBody(),currentScope);
        }
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
        Symbol var = new VarSymbol(node.getVariable().getName(),node.getVariable().getTypes());
        if(currentScope.checkIfAlreadyDefined(var.getName())){
            throw new VariableAlreadyDefinedException(node.getVariable().getName());
        }
        currentScope.defineSymbol(var);
        node.getInitializer().ifPresent(e->e.visit(this));
    }

}

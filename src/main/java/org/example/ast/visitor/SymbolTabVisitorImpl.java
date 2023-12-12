package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;
import org.example.exceptions.FunctionAlreadyDefinedException;
import org.example.handler.VisitorHandler;
import org.example.exceptions.VariableAlreadyDefinedException;
import org.example.exceptions.VariableNotDefinedException;
import org.example.scope.LocalScope;
import org.example.scope.Scope;
import org.example.symbol.*;
import org.example.token.TokenType;

import java.util.List;
import java.util.Objects;

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
        List<Symbol> args = node.getParameters()
                .stream()
                .map(e->(Symbol)new VarSymbol(e.getName(),e.getGuardExpression().orElse(null)))
                .toList();
        var func = new FunctionSymbol(node.getToken().getValue(), args, node.getReturnType().orElse(null), node.getBody(), currentScope);
        Symbol symbol = currentScope.resolve(node.getToken().getValue());
        if((symbol instanceof FunctionAggregateSymbol existing)){
            String existingTypeName =
                    existing.getFunctionSymbols().get(0).getSpecifierNode() != null ?
                            existing.getFunctionSymbols().get(0).getSpecifierNode().getTypeName()
                            : null;
            String nodeReturnTypeName = node.getReturnType().map(TypeSpecifierNode::getTypeName).orElse(null);
            if(!Objects.equals(existingTypeName,nodeReturnTypeName)){
                throw new FunctionAlreadyDefinedException(node.getFunctionSymbol().getName(),node.getToken().getLine());
            }
            existing.addFunctionSymbol(func);
        }else {
            FunctionAggregateSymbol newAggregate = new FunctionAggregateSymbol(node.getToken().getValue());
            newAggregate.addFunctionSymbol(func);
            currentScope.defineSymbol(newAggregate);
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
            throw new VariableNotDefinedException(node.getValue(),node.getToken().getLine());
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
    public void visit(VariableDefNode node) {
        Symbol var = new VarSymbol(node.getVariable().getName(), node.getVariable().getGuardExpression().orElse(null));
        if(currentScope.checkIfAlreadyDefined(var.getName())){
            throw new VariableAlreadyDefinedException(node.getVariable().getName(),node.getVariable().getToken().getLine());
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
            throw new VariableAlreadyDefinedException(node.getName(),node.getToken().getLine());
        }
        List<Symbol> args = node.getConstructorParams()
                .stream()
                .map(e->(Symbol)new VarSymbol(e.getName(),e.getGuardExpression().orElse(null)))
                .toList();
        StructureSymbol struct = new StructureSymbol(args, node.getName(),currentScope, node.getBody());
        currentScope.defineSymbol(struct);
        currentScope = struct;
        var ths = new VarSymbol(TokenType.THIS_KEYWORD.getRegex(),null);
        ths.setType(struct);
        currentScope.defineSymbol(ths);
        node.getBody().visit(this);
        node.getBody().getScope().getSymbols().forEach(symbol -> classifySymbol(symbol,struct));
        node.setSymbol(struct);
        currentScope = currentScope.getUpperScope();
    }

    private void classifySymbol(Symbol symbol, StructureSymbol structureSymbol)  {
        if(symbol instanceof FunctionAggregateSymbol functionSymbol) {
            structureSymbol.getFunctions().put(functionSymbol.getName(),functionSymbol);
        }else {
            structureSymbol.getFields().put(symbol.getName(),symbol);
        }
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

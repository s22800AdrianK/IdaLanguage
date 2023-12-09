package org.example.symbol;

import org.example.ast.BlockNode;
import org.example.ast.TypeSpecifierNode;
import org.example.scope.Scope;
import org.example.type.Type;

import java.util.*;

public class FunctionSymbol extends Symbol implements Scope {
    private final List<Symbol> arguments;
    private final BlockNode body;
    private final Scope upperScope;
    private final TypeSpecifierNode specifierNode;
    public FunctionSymbol(String name, List<Symbol> arguments, TypeSpecifierNode specifier, BlockNode body, Scope upperScope) {
        super(name);
        this.arguments = arguments;
        this.body = body;
        this.upperScope = upperScope;
        this.specifierNode = specifier;
    }

    @Override
    public Scope getUpperScope() {
        return this.upperScope;
    }

    @Override
    public void defineSymbol(Symbol symbol) {
        throw new RuntimeException();
    }

    @Override
    public Symbol resolve(String name) {
        return arguments
                .stream()
                .filter(e->e.getName().equals(name))
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolve(name) : null);
    }

    @Override
    public Type resolveType(String name) {
        return arguments
                .stream()
                .filter(e->e.getName().equals(name)&&e instanceof Type)
                .map(e->(Type)e)
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolveType(name) : null);
    }

    @Override
    public boolean checkIfAlreadyDefined(String name) {
        return this.resolve(name)!=null || this.upperScope.checkIfAlreadyDefined(name);
    }

    @Override
    public List<Symbol> getSymbols() {
        return arguments;
    }


    public TypeSpecifierNode getSpecifierNode() {
        return specifierNode;
    }

    public BlockNode getBody() {
        return body;
    }
}

package org.example.symbol;

import org.example.ast.BlockNode;
import org.example.scope.Scope;
import org.example.type.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionSymbol extends Symbol implements Scope {
    private final Map<List<Symbol>,BlockNode> implementations = new HashMap<>();
    private final Scope upperScope;

    public FunctionSymbol(String name, Type type,List<Symbol> arguments, BlockNode body, Scope upperScope) {
        super(name, type);
        implementations.put(arguments,body);
        this.upperScope = upperScope;
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
        return implementations.keySet()
                .stream().flatMap(Collection::stream)
                .filter(e->e.getName().equals(name))
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolve(name) : null);
    }

    @Override
    public List<FunctionSymbol> resolveFunctions() {
        return List.of(this);
    }

    @Override
    public Type resolveType(String name) {
        return implementations.keySet()
                .stream().flatMap(Collection::stream)
                .filter(e->e.getName().equals(name)&&e instanceof Type)
                .map(e->(Type)e)
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolveType(name) : null);
    }

    public void addNewImplementation(List<Symbol> args,BlockNode body) {
        this.implementations.put(args,body);
    }

    @Override
    public boolean checkIfAlreadyDefined(String name) {
        Scope current = this;
        while (current!=null){
            if(current.resolve(name)!=null) {
                return true;
            }
            current = current.getUpperScope();
        }
        return false;
    }

    public Map<List<Symbol>, BlockNode> getImplementations() {
        return implementations;
    }
}

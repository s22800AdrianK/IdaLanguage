package org.example.symbol;


import org.example.ast.BlockNode;
import org.example.scope.Scope;
import org.example.type.Type;

import java.util.*;

public class StructureSymbol extends Symbol implements Type, Scope {
    private final Map<String,Symbol> fields = new HashMap<>();
    private final Map<String,FunctionAggregateSymbol> functions = new HashMap<>();
    private final List<Symbol> constructorArgs;
    private final String name;
    private final Scope upperScope;
    private final BlockNode body;
    public StructureSymbol(List<Symbol> constructorArgs, String name, Scope upperScope, BlockNode body) {
        super(name);
        this.constructorArgs = constructorArgs;
        this.name = name;
        this.upperScope = upperScope;
        this.body = body;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scope getUpperScope() {
        return upperScope;
    }

    @Override
    public void defineSymbol(Symbol symbol) {
        fields.put(symbol.getName(),symbol);
    }

    @Override
    public Symbol resolve(String name) {
        if(fields.containsKey(name)) {
            return fields.get(name);
        }
        if(functions.containsKey(name)) {
            return functions.get(name);
        }
        return constructorArgs.stream()
                .filter(e->e.getName().equals(name))
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolve(name) : null);
    }

    @Override
    public Type resolveType(String name) {
        return upperScope.resolveType(name);
    }

    @Override
    public boolean checkIfAlreadyDefined(String name) {
        if(fields.containsKey(name)){
            return true;
        }
        if(functions.containsKey(name)){
            return true;
        }
        return constructorArgs.stream().map(Symbol::getName).anyMatch(e -> e.equals(name));
    }

    public boolean checkIfHasFieldOrFunction(String name) {
        if(fields.containsKey(name)){
            return true;
        }
        if(functions.containsKey(name)){
            return true;
        }
        return constructorArgs.stream().map(Symbol::getName).anyMatch(e -> e.equals(name));
    }
    @Override
    public List<Symbol> getSymbols() {
        var symbols = new ArrayList<>(fields.values());
        symbols.addAll(constructorArgs);
        return symbols;
    }
    public Map<String, FunctionAggregateSymbol> getFunctions() {
        return functions;
    }
    public Map<String, Symbol> getFields(){ return fields;}
    public List<Symbol> getConstructorArgs() {
        return constructorArgs;
    }

    public BlockNode getBody() {
        return body;
    }
}

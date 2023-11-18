package org.example.symbol;


import org.example.scope.Scope;
import org.example.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureSymbol extends Symbol implements Type, Scope {
    private final Map<String,Symbol> fields = new HashMap<>();
    private final Map<String,FunctionSymbol> functions = new HashMap<>();
    private final List<Symbol> constructorArgs;
    private final String name;
    private final Scope upperScope;

    public StructureSymbol(List<Symbol> constructorArgs, String name, Scope upperScope) {
        super(name);
        this.constructorArgs = constructorArgs;
        this.name = name;
        this.upperScope = upperScope;
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
        return fields.get(name);
    }

    @Override
    public Type resolveType(String name) {
        return null;
    }

    @Override
    public boolean checkIfAlreadyDefined(String name) {
        if(fields.containsKey(name)){
            return true;
        }
        if(functions.containsKey(name)){
            return true;
        }
        return constructorArgs.stream().map(Symbol::getName).anyMatch(e -> e.equals(name)) || upperScope.checkIfAlreadyDefined(name);
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
        return fields.values().stream().toList();
    }
    public Map<String, FunctionSymbol> getFunctions() {
        return functions;
    }
    public Map<String, Symbol> getFields(){ return fields;}
    public List<Symbol> getConstructorArgs() {
        return constructorArgs;
    }
}

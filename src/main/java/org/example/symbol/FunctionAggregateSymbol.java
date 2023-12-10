package org.example.symbol;

import org.example.scope.CoreScope;
import org.example.scope.LocalScope;
import org.example.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public class FunctionAggregateSymbol extends Symbol {
    private final List<FunctionSymbol> functionSymbols = new ArrayList<>();

    public FunctionAggregateSymbol(String name) {
        super(name);
    }

    public void addFunctionSymbol(FunctionSymbol functionSymbol) {
        functionSymbols.add(functionSymbol);
    }

    public List<FunctionSymbol> getFunctionSymbols() {
        return functionSymbols;
    }

    public Scope getAggregatedScope(Scope upper) {
        var core = new LocalScope(upper);
        functionSymbols.stream().flatMap(fn->fn.getSymbols().stream())
                .forEach(core::defineSymbol);
        return core;
    }
}

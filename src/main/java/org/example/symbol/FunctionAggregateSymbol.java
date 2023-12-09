package org.example.symbol;

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
}

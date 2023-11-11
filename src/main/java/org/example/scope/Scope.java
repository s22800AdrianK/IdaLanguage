package org.example.scope;

import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.type.Type;

import java.util.List;

public interface Scope {
    Scope getUpperScope();
    void defineSymbol(Symbol symbol);
    Symbol resolve(String name);
    List<FunctionSymbol> resolveFunctions();
    Type   resolveType(String name);
    boolean checkIfAlreadyDefined(String name);
    List<Symbol> getAllSymbols();
    List<Symbol> getSymbols();
}

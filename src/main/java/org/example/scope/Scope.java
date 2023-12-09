package org.example.scope;

import org.example.symbol.Symbol;
import org.example.type.Type;

import java.util.List;

public interface Scope {
    Scope getUpperScope();
    void defineSymbol(Symbol symbol);
    Symbol resolve(String name);
    Type   resolveType(String name);
    boolean checkIfAlreadyDefined(String name);
    List<Symbol> getSymbols();
}

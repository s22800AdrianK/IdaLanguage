package org.example.scope;

import org.example.symbol.Symbol;

public interface Scope {
    Scope getUpperScope();
    void defineSymbol(Symbol symbol);
    Symbol resolve(String name);
}

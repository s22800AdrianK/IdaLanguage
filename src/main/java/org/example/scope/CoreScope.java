package org.example.scope;

import org.example.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class CoreScope implements Scope{
    List<Symbol> symbols = new ArrayList<>();
    @Override
    public Scope getUpperScope() {
        return null;
    }
    @Override
    public void defineSymbol(Symbol symbol) {
        symbols.add(symbol);
    }
    @Override
    public Symbol resolve(String name) {
        return symbols.stream().filter(e->e.getName().equals(name)).findFirst().orElseThrow(RuntimeException::new);
    }
}

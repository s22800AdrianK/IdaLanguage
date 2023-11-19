package org.example.scope;

import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class CoreScope implements Scope {
    private final List<Symbol> symbols = new ArrayList<>();
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
        return symbols.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolve(name) : null);
    }

    @Override
    public Type resolveType(String name) {
        return symbols.stream()
                .filter(e -> e.getName().equals(name)&&e instanceof Type)
                .map(e->(Type)e)
                .findFirst()
                .orElse(getUpperScope() != null ? getUpperScope().resolveType(name) : null);
    }
    @Override
    public boolean checkIfAlreadyDefined(String name) {
        return this.resolve(name)!=null ||
                (this.getUpperScope()!=null && this.getUpperScope().checkIfAlreadyDefined(name));
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }
}

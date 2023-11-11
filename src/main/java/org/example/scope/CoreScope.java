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
    public List<FunctionSymbol> resolveFunctions() {
        return null;
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
        Scope current = this;
        while (current!=null){
            if(current.resolve(name)!=null) {
                return true;
            }
            current = current.getUpperScope();
        }
        return false;
    }

    @Override
    public List<Symbol> getAllSymbols() {
        List<Symbol> allSymbols = new ArrayList<>();
        Scope current = this;
        while(current!=null){
            allSymbols.addAll(current.getSymbols());
            current = current.getUpperScope();
        }
        return allSymbols;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }
}

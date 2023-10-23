package org.example.symbol;

import org.example.scope.Scope;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class FunctionSymbol extends Symbol implements Scope {
    private final List<Symbol> arguments = new ArrayList<>();
    int a;
    private final Scope upperScope;
    public FunctionSymbol(String name, Type type, Scope upperScope) {
        super(name, type);
        this.upperScope = upperScope;
    }
    @Override
    public Scope getUpperScope() {
        return this.upperScope;
    }
    @Override
    public void defineSymbol(Symbol symbol) {
        arguments.add(symbol);
    }
    @Override
    public Symbol resolve(String name) {
        return arguments.stream().filter(e->e.getName().equals(name)).findFirst().orElseThrow(RuntimeException::new);
    }
}

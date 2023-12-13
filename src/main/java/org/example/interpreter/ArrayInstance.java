package org.example.interpreter;

import org.example.symbol.Symbol;
import org.example.symbol.builtIn.BuiltInTypeSymbol;

import java.util.ArrayList;
import java.util.List;

public class ArrayInstance extends StructureInstance{
    private final List<Object> values = new ArrayList<>();

    public ArrayInstance(BuiltInTypeSymbol symbol) {
        super(symbol);
    }
    public List<Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}

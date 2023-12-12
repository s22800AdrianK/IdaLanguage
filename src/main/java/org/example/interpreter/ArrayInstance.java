package org.example.interpreter;

import org.example.symbol.StructureSymbol;

import java.util.ArrayList;
import java.util.List;

public class ArrayInstance extends StructureInstance{
    private final List<Object> values = new ArrayList<>();

    public ArrayInstance() {
        super();
    }
    public List<Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}

package org.example.symbol;

import org.example.type.Type;

import java.util.List;

public class VarSymbol extends Symbol {
    public VarSymbol(String name, List<Type> type) {
        super(name, type);
    }
}

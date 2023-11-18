package org.example.symbol.builtIn;

import org.example.scope.Scope;
import org.example.symbol.StructureSymbol;
import org.example.type.Type;
import org.example.type.TypeOperation;

import java.util.ArrayList;
import java.util.List;

public class BuiltInTypeSymbol extends StructureSymbol implements Type {
    private final List<TypeOperation> operations = new ArrayList<>();
    public BuiltInTypeSymbol(String name, Scope scope) {
        super(constructor, name,scope);
    }

    public List<TypeOperation> getOperations() {
        return operations;
    }
}

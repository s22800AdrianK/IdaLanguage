package org.example.symbol.builtIn;

import org.example.scope.Scope;
import org.example.type.Type;

public class ArrayBuiltInTypeSymbol extends BuiltInTypeSymbol {
    private Type baseType;
    public ArrayBuiltInTypeSymbol(String name, Scope scope, Type baseType) {
        super(name, scope);
        this.baseType = baseType;
    }

    @Override
    public void setupOperations() {

    }

    public Type getBaseType() {
        return baseType;
    }

    public void setBaseType(BuiltInTypeSymbol baseType) {
        this.baseType = baseType;
    }

    public static ArrayBuiltInTypeSymbol of(Type type,Scope scope) {
        return new ArrayBuiltInTypeSymbol(type.getName()+"[]",scope,type);
    }
}

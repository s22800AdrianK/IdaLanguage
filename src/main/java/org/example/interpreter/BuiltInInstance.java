package org.example.interpreter;

import org.example.symbol.builtIn.BuiltInTypeSymbol;

public class BuiltInInstance extends StructureInstance{

    public BuiltInInstance(Object value, BuiltInTypeSymbol symbol) {
        super(symbol);
        this.getFields().put("value",value);
    }
    @Override
    public String toString(){
        return this.getFields().get("value").toString();
    }
}

package org.example.symbol;

import org.example.type.Type;

public class Symbol {
    private String name;
    private Type type;
    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    public Symbol(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

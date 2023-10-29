package org.example.symbol;

import org.example.type.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Symbol {
    private String name;
    private List<Type> types;

    public Symbol(String name, List<Type> type) {
        this.name = name;
        this.types = type;
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

    public List<Type> getType() {
        return types;
    }

    public void setType(List<Type> types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(name, symbol.name)
                && (types.size() == symbol.types.size())
                && new HashSet<>(types).containsAll(symbol.types)
                && new HashSet<>(symbol.types).containsAll(types);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, types);
    }
}

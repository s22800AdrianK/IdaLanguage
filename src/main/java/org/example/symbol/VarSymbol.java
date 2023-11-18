package org.example.symbol;

import org.example.ast.ExpressionNode;
import org.example.type.Type;

import java.util.Objects;
import java.util.Optional;

public class VarSymbol extends Symbol {
    private final ExpressionNode guardExpr;
    public VarSymbol(String name, ExpressionNode guardExpr) {
        super(name);
        this.guardExpr = guardExpr;
    }

    public Optional<ExpressionNode> getGuardExpr() {
        return Optional.ofNullable(guardExpr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VarSymbol varSymbol = (VarSymbol) o;
        return Objects.equals(guardExpr, varSymbol.guardExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), guardExpr);
    }
}

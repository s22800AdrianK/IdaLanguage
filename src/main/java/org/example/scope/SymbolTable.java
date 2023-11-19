package org.example.scope;

import org.example.symbol.builtIn.BoolBuiltInTypeSymbol;
import org.example.symbol.builtIn.BuiltInTypeSymbol;
import org.example.symbol.builtIn.NumBuiltInTypeSymbol;
import org.example.symbol.builtIn.StrBuiltInTypeSymbol;
import org.example.type.Type;

import java.util.List;

public class SymbolTable extends CoreScope {

    public SymbolTable() {
        super();
        initDefaultSymbols();
    }
    private void initDefaultSymbols() {
        this.defineSymbol(new StrBuiltInTypeSymbol(this));
        this.defineSymbol(new BoolBuiltInTypeSymbol(this));
        this.defineSymbol(new NumBuiltInTypeSymbol(this));
        getBuiltInTypes().forEach(BuiltInTypeSymbol::setupOperations);
    }

    public BuiltInTypeSymbol getBuiltInTypeForName(String name) {
        return  this.getSymbols().stream()
                .filter(BuiltInTypeSymbol.class::isInstance)
                .map(BuiltInTypeSymbol.class::cast)
                .findFirst().orElseThrow(RuntimeException::new);
    }
    public List<BuiltInTypeSymbol> getBuiltInTypes() {
        return this.getSymbols().stream()
                .filter(BuiltInTypeSymbol.class::isInstance)
                .map(BuiltInTypeSymbol.class::cast)
                .toList();
    }
}

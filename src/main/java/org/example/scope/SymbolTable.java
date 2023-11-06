package org.example.scope;

import org.example.symbol.BuiltInTypeSymbol;
import org.example.token.TokenType;
import org.example.type.Type;

public class SymbolTable extends CoreScope {

    public SymbolTable() {
        super();
        initDefaultSymbols();
    }
    private void initDefaultSymbols() {
        this.defineSymbol(new BuiltInTypeSymbol(TokenType.TYPE_NUMBER.getRegex()));
        this.defineSymbol(new BuiltInTypeSymbol(TokenType.TYPE_STRING.getRegex()));
        this.defineSymbol(new BuiltInTypeSymbol(TokenType.TYPE_BOOL.getRegex()));
    }

    public Type getBuliInTypeForName(String name) {
        return (Type) this.getSymbols().stream().filter(e ->
                (e instanceof BuiltInTypeSymbol && e.getName().equals(name))
        ).findFirst().orElseThrow(RuntimeException::new);
    }
}

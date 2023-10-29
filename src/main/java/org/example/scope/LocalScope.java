package org.example.scope;

public class LocalScope extends CoreScope {
    private final Scope upperScope;

    public LocalScope(Scope upperScope) {
        this.upperScope = upperScope;
    }

    @Override
    public Scope getUpperScope() {
        return this.upperScope;
    }
}

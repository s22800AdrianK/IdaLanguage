package org.example.interpreter;

import org.example.scope.Scope;

public interface MemorySpace {
    void setVariable(String name, Object value);
    Object getVariable(String name);
    void pop();
    void pushScope(Scope scope);
}

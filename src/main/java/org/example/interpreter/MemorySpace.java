package org.example.interpreter;

import org.example.scope.Scope;

import java.util.Map;

public interface MemorySpace {
    void setVariable(String name, Object value);
    Object getVariable(String name);
    Map<String,Object> pop();
    void pushScope(Scope scope);
}

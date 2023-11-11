package org.example.interpreter;

import org.example.scope.Scope;
import org.example.symbol.Symbol;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MemorySpaceImpl implements MemorySpace {
    private Deque<Map<String, Object>> stack = new LinkedList<>();

    public void setVariable(String name, Object value) {
        for (Map<String, Object> scope : stack) {
            if (scope.containsKey(name)) {
                scope.put(name,value);
            }
        }
    }

    public Object getVariable(String name) {
        for (Map<String, Object> scope : stack) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }


    @Override
    public void pop() {
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    @Override
    public void pushScope(Scope scope) {
        Map<String, Object> scopedVariables = new HashMap<>();
        scope.getSymbols().forEach(e->scopedVariables.put(e.getName(),null));
        stack.push(scopedVariables);
    }


}

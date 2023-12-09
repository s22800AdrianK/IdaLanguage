package org.example.interpreter;

import org.example.scope.Scope;
import org.example.symbol.Symbol;
import org.example.symbol.builtIn.BuiltInTypeSymbol;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MemorySpaceImpl implements MemorySpace {
    private final Deque<Map<String, Object>> stack = new LinkedList<>();

    @Override
    public void setVariable(String name, Object value) {
        stack.stream()
                .filter(s->s.containsKey(name))
                .findFirst()
                .ifPresent(s->s.put(name,value));
    }
    @Override
    public Object getVariable(String name) {
        return stack.stream()
                .filter(s->s.containsKey(name))
                .findFirst()
                .map(s->s.get(name))
                .orElse(null);
    }


    @Override
    public Map<String, Object> pop() {
        return !stack.isEmpty()? stack.pop() : null;
    }

    @Override
    public void pushScope(Scope scope) {
        Map<String, Object> scopedVariables = new HashMap<>();
        scope.getSymbols().forEach(e->scopedVariables.put(e.getName(),null));
        stack.push(scopedVariables);
    }



}

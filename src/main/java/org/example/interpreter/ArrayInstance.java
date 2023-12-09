package org.example.interpreter;

import java.util.ArrayList;
import java.util.List;

public class ArrayInstance {
    private final List<Object> values = new ArrayList<>();
    public List<Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}

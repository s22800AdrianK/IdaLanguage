package org.example.interpreter;
import org.example.symbol.StructureSymbol;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StructureInstance {
    private final Map<String,Object> fields = new HashMap<>();
    private final StructureSymbol struct;

    public StructureInstance(StructureSymbol structureSymbol) {
        structureSymbol.getFields().forEach((k,v)->this.fields.put(k,null));
        structureSymbol.getConstructorArgs().forEach((k)->this.fields.put(k.getName(),null));
        this.fields.put("this",this);
        this.struct = structureSymbol;
    }
    public Map<String, Object> getFields() {
        return fields;
    }

    public StructureSymbol getStruct() {
        return struct;
    }
}

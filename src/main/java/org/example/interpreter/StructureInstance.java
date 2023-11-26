package org.example.interpreter;

import org.example.ast.StructureNode;
import org.example.symbol.StructureSymbol;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StructureInstance {
    private final Map<String,Object> fields;

    public StructureInstance(Map<String,Object> fields, List<Object> evaluatedArgs,StructureSymbol structureSymbol) {
        Map<String,Object> args = IntStream.range(0,evaluatedArgs.size())
                .mapToObj(i->new AbstractMap.SimpleEntry<>(structureSymbol.getConstructorArgs().get(i).getName(),evaluatedArgs.get(i)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,(existing, replace) -> existing));
        args.forEach((key,value)->fields.merge(key,value,(v1,v2)->v1));
        this.fields = fields;
    }

    private Map<String, Object> retriveFields(StructureSymbol structureSymbol) {
        Map<String,Object> ret = new HashMap<>();
        for (String name: structureSymbol.getFields().keySet()) {
            ret.put(name,null);
        }
        return ret;
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}

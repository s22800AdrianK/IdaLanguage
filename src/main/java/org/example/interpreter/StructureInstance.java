package org.example.interpreter;
import org.example.symbol.StructureSymbol;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StructureInstance {
    private final Map<String,Object> fields;

    public StructureInstance(Map<String,Object> fields, List<Object> evaluatedArgs,StructureSymbol structureSymbol) {
        Map<String,Object> args = IntStream.range(0,evaluatedArgs.size())
                .mapToObj(i->new AbstractMap.SimpleEntry<>(structureSymbol.getConstructorArgs().get(i).getName(),evaluatedArgs.get(i)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,(existing, replace) -> existing));
        args.forEach((key,value)->fields.merge(key,value,(v1,v2)->v1));
        this.fields = fields;
    }
    public Map<String, Object> getFields() {
        return fields;
    }
}

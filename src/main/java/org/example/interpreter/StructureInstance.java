package org.example.interpreter;

import org.example.ast.StructureNode;
import org.example.symbol.StructureSymbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StructureInstance {
    private final Map<String,Object> fields;

    public StructureInstance(StructureSymbol structureSymbol, List<Object> evaluatedArgs) {
        Stream<Map.Entry<String,Object>> args = IntStream.range(0,evaluatedArgs.size())
                .mapToObj(i->Map.entry(structureSymbol.getConstructorArgs().get(i).getName(),evaluatedArgs.get(i)));
        Stream<Map.Entry<String,Object>> fields = structureSymbol.getFields().keySet()
                .stream()
                .map(symbol -> Map.entry(symbol, null));
        this.fields =  Stream.concat(args,fields)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing,replace) -> existing
                        ));
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}

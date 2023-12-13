package org.example.symbol.builtIn;

import org.example.helpers.BuiltInFunctionFactory;
import org.example.interpreter.ArrayInstance;
import org.example.scope.Scope;
import org.example.symbol.VarSymbol;
import org.example.token.TokenType;
import org.example.type.Type;

import java.math.BigDecimal;
import java.util.List;

public class ArrayBuiltInTypeSymbol extends BuiltInTypeSymbol {
    private Type baseType;
    private ArrayBuiltInTypeSymbol(String name, Scope scope, Type baseType) {
        super(name, scope);
        this.baseType = baseType;
        setupFunctions();
    }
    @Override
    public void setupOperations() {}
    public Type getBaseType() {
        return baseType;
    }
    public void setBaseType(BuiltInTypeSymbol baseType) {
        this.baseType = baseType;
    }
    public static ArrayBuiltInTypeSymbol of(Type type,Scope scope) {
        return new ArrayBuiltInTypeSymbol(type.getName()+"[]",scope,type);
    }
    private void setupFunctions() {
        this.getFunctions().put("push", BuiltInFunctionFactory.createBuiltInFunction(
                "push",
                null,
                List.of(new VarSymbol(this.getBaseType(),"newValue")),
                memorySpace -> {
                    Object value = memorySpace.getVariable("newValue");
                    return ((ArrayInstance)memorySpace.getVariable("this")).getValues().add(value);
                }
        ));
        this.getFunctions().put("remove", BuiltInFunctionFactory.createBuiltInFunction(
                "remove",
                null,
                List.of(new VarSymbol(this.getBaseType(),"index")),
                memorySpace -> {
                    int index = ((BigDecimal)memorySpace.getVariable("index")).intValue()-1;
                    return ((ArrayInstance)memorySpace.getVariable("this")).getValues().remove(index);
                }
        ));
    }
}

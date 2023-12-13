package org.example.helpers;

import org.example.ast.BlockNode;
import org.example.interpreter.IdaInterpreter;
import org.example.interpreter.MemorySpace;
import org.example.symbol.FunctionAggregateSymbol;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.type.Type;

import java.util.List;
import java.util.function.Function;

public class BuiltInFunctionFactory {
    public static FunctionAggregateSymbol createBuiltInFunction(String name, Type type,List<Symbol> arguments, Function<MemorySpace,Object> function) {
        MockBody body = new MockBody(function);
        FunctionAggregateSymbol functionAggr = new FunctionAggregateSymbol(name);
        functionAggr.addFunctionSymbol(new MockFunctionSymbol(name,arguments,type,body));
        return functionAggr;
    }

    private static class MockFunctionSymbol extends FunctionSymbol {
        public MockFunctionSymbol(String name, List<Symbol> arguments, Type type,MockBody body) {
            super(name, arguments, null, body, null);
            this.setType(type);
        }
    }

    private static class MockBody extends BlockNode {
        private final Function<MemorySpace,Object> function;
        public MockBody(Function<MemorySpace, Object> function) {
            super(null);
            this.function = function;
        }
        @Override
        public Object execute(IdaInterpreter interpreter) {
            return function.apply(interpreter.getMemorySpace());
        }
    }
}

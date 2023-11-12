package org.example.interpreter;

import org.example.ast.BlockNode;
import org.example.ast.ExpressionNode;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;

import java.util.List;
import java.util.Map;

public class FunctionCallEvaluator {

    public Map.Entry<List<Symbol>,BlockNode> eval(FunctionSymbol fun, List<Object> evaluatedArgs, IdaInterpreter interpreter, MemorySpace space){
        return fun.getImplementations()
                .entrySet()
                .stream()
                .filter(entry -> matchesArguments(entry.getKey(), evaluatedArgs, interpreter, space))
                .findFirst()
                .orElse(null);
    }


    private boolean matchesArguments(List<Symbol> parameters, List<Object> evaluatedArgs, IdaInterpreter interpreter, MemorySpace space) {

        for (int i = 0; i < parameters.size(); i++) {
            VarSymbol var = (VarSymbol) parameters.get(i);
            space.setVariable(var.getType().getName(), evaluatedArgs.get(i));
            if (var.getGuardExpr().isPresent() && !evaluateGuardExpression(var.getGuardExpr().get(), interpreter)) {
                return false;
            }
        }

        return true;
    }

    public boolean evaluateGuardExpression(ExpressionNode guardExpr, IdaInterpreter interpreter) {
        Object result = guardExpr.execute(interpreter);
        return (Boolean) result;
    }
}

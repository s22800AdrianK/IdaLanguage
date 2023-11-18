package org.example.interpreter;

import org.example.ast.BlockNode;
import org.example.ast.ExpressionNode;
import org.example.ast.FunctionCallNode;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FunctionCallEvaluator {
    private ExpressionEvaluator evaluator;
    private IdaInterpreter interpreter;
    private MemorySpace memorySpace;
    public FunctionCallEvaluator(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Map.Entry<List<Symbol>,BlockNode> eval(FunctionSymbol fun, List<Object> evaluatedArgs, MemorySpace space){
        return fun.getImplementations()
                .entrySet()
                .stream()
                .filter(entry -> matchesArguments(entry.getKey(), evaluatedArgs, space))
                .findFirst()
                .orElse(null);
    }


    public boolean matchesArguments(List<Symbol> parameters, List<Object> evaluatedArgs, MemorySpace space) {

        for (int i = 0; i < parameters.size(); i++) {
            VarSymbol var = (VarSymbol) parameters.get(i);
            space.setVariable(var.getType().getName(), evaluatedArgs.get(i));
            if (var.getGuardExpr().isPresent() && !evaluator.evaluateGuardExpression(var.getGuardExpr().get())) {
                return false;
            }
        }

        return true;
    }

    public List<Object> evaluateArguments(FunctionCallNode node) {
        return node.getArguments().stream()
                .map(arg -> arg.execute(interpreter))
                .collect(Collectors.toList());
    }

    public void assignArgumentsToParameters(Map.Entry<List<Symbol>, BlockNode> functionExecutionResult, List<Object> evaluatedArgs) {
        IntStream.range(0, evaluatedArgs.size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(functionExecutionResult.getKey().get(i), evaluatedArgs.get(i)))
                .forEach(entry -> memorySpace.setVariable(entry.getKey().getName(), entry.getValue()));
    }

    public void setInterpreter(IdaInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void setEvaluator(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void setMemorySpace(MemorySpace memorySpace) {
        this.memorySpace = memorySpace;
    }
}

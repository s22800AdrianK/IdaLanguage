package org.example.interpreter;

import org.example.ast.ExpressionNode;
import org.example.exceptions.ViolatedGuardException;
import org.example.symbol.VarSymbol;

public class ExpressionEvaluator {
    private MemorySpace memorySpace;
    private IdaInterpreter interpreter;
    public void executeWithGuard(VarSymbol var, Object value, ExpressionNode expressionNode) {
        memorySpace.setVariable(var.getType().getName(), value);
        if (!evaluateGuardExpression(expressionNode)) {
            throw new ViolatedGuardException(var.getName(),expressionNode.getToken().getLine());
        }
        memorySpace.setVariable(var.getType().getName(), null);
    }
    public boolean evaluateGuardExpression(ExpressionNode guardExpr) {
        Object result = guardExpr.execute(interpreter);
        return (Boolean) result;
    }

    public void setMemorySpace(MemorySpace memorySpace) {
        this.memorySpace = memorySpace;
    }

    public void setInterpreter(IdaInterpreter interpreter) {
        this.interpreter = interpreter;
    }
}

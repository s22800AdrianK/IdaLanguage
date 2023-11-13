package org.example.interpreter;

import org.example.ast.*;
import org.example.ast.PrimaryExNode;
import org.example.handler.Handler;
import org.example.scope.Scope;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class IdaInterpreterImpl implements IdaInterpreter, Handler {
    private final MemorySpace memorySpace;
    private final BinaryOperationEvaluator binaryOperationEvaluator;
    private final FunctionCallEvaluator functionCallEvaluator;
    private final ExpressionEvaluator expressionEvaluator;
    private Scope currentScope;

    public IdaInterpreterImpl(MemorySpace memorySpace, BinaryOperationEvaluator binaryOperationEvaluator, FunctionCallEvaluator evaluator, ExpressionEvaluator expressionEvaluator, Scope currentScope) {
        this.memorySpace = memorySpace;
        this.binaryOperationEvaluator = binaryOperationEvaluator;
        this.functionCallEvaluator = evaluator;
        evaluator.setEvaluator(expressionEvaluator);
        evaluator.setMemorySpace(memorySpace);
        evaluator.setInterpreter(this);
        this.expressionEvaluator = expressionEvaluator;
        this.expressionEvaluator.setInterpreter(this);
        this.expressionEvaluator.setMemorySpace(this.memorySpace);
        this.currentScope = currentScope;
    }

    @Override
    public Object execute(ProgramNode node) {
        node.getStatements().forEach(e->e.execute(this));
        return null;
    }

    @Override
    public Object execute(AssignmentNode node) {
        Object value = node.getExpression().execute(this);
        VarSymbol var = (VarSymbol) currentScope.resolve(node.getVariableName());
        var.getGuardExpr().ifPresent(expressionNode -> expressionEvaluator.executeWithGuard(var,value,expressionNode));
        memorySpace.setVariable(node.getVariableName(),value);
        return null;
    }

    @Override
    public Object execute(BlockNode node) {
        pushScope(node.getScope());
        Object lastValue = null;
        for (StatementNode e : node.getStatements()) {
            lastValue = e.execute(this);
        }
        popScope(node.getScope());
        return lastValue;
    }

    @Override
    public Object execute(IfStatementNode node) {
        if((boolean)node.getCondition().execute(this)) {
            node.getThenBlock().execute(this);
        }else {
            node.getElseBlock().ifPresent(e->e.execute(this));
        }
        return null;
    }

    @Override
    public Object execute(PrintStatementNode node) {
        System.out.println(node.getExpression().execute(this));
        return null;
    }

    @Override
    public Object execute(ExpressionNode expressionNode) {
        return null;
    }

    @Override
    public Object execute(FunctionCallNode node) {
        FunctionSymbol fun = (FunctionSymbol) currentScope.resolve(node.getName());
        var evaluatedArgs =functionCallEvaluator.evaluateArguments(node);
        pushScope(fun);
        Map.Entry<List<Symbol>,BlockNode> finalFun = functionCallEvaluator.eval(fun,evaluatedArgs,memorySpace);
        if(finalFun == null) {
            throw new RuntimeException("AAAAAAAAAA");
        }
        functionCallEvaluator.assignArgumentsToParameters(finalFun,evaluatedArgs);
        Object ret = null;
        if(fun.getType()!=null) {
            ret = finalFun.getValue().execute(this);
        }else {
            finalFun.getValue().execute(this);
        }
        popScope(fun);
        return ret;
    }

    @Override
    public Object execute(BinaryOpNode node) {
        Object left = node.getLeft().execute(this);
        Object right = node.getRight().execute(this);
        return binaryOperationEvaluator.evaluateExpr(node.getOperator(), node.getEvalType(),left,right);
    }

    @Override
    public Object execute(PrimaryExNode node) {
        return switch (node.getToken().getType()){
            case NUMBER -> new BigDecimal(node.getValue());
            case STRING -> node.getValue();
            case BOOL -> node.getValue().equals("true");
            case NAME -> memorySpace.getVariable(node.getValue());
            default -> null;
        };
    }

    @Override
    public Object execute(PrimaryGuardNode node) {
        return memorySpace.getVariable(node.getToken().getValue());
    }

    @Override
    public Object execute(FunctionDefNode node) {
        return null;
    }

    @Override
    public Object execute(ParameterNode node) {
        return null;
    }

    @Override
    public Object execute(TypeSpecifierNode node) {
        return null;
    }

    @Override
    public Object execute(VariableDefNode node) {
        node.getInitializer().ifPresent(expressionNode->{
            Object value = expressionNode.execute(this);
            VarSymbol var = (VarSymbol) currentScope.resolve(node.getVariable().getName());
            var.getGuardExpr().ifPresent(guard -> expressionEvaluator.executeWithGuard(var,value,guard));
            memorySpace.setVariable(node.getVariable().getName(),value);
        });
        return null;
    }

    @Override
    public Object execute(StatementNode node) {
        return null;
    }

    @Override
    public Object execute(WhileStatementNode node) {
        while ((boolean)node.getCondition().execute(this)) {
            node.getThenBlock().execute(this);
        }
        return null;
    }

    private void pushScope(Scope scope) {
        memorySpace.pushScope(scope);
        currentScope = scope;
    }

    private void popScope(Scope scope) {
        memorySpace.pop();
        currentScope = scope.getUpperScope();
    }

    @Override
    public void setNextHandler(Handler handler) {}

    @Override
    public void handle(ProgramNode programNode) {
        pushScope(currentScope);
        programNode.execute(this);
    }
}

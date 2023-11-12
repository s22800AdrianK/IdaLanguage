package org.example.interpreter;

import org.example.ast.*;
import org.example.ast.PrimaryExNode;
import org.example.scope.Scope;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class IdaInterpreterImpl implements IdaInterpreter {
    private final MemorySpace memorySpace;
    private final BinaryOperationEvaluator binaryOperationEvaluator;
    private final FunctionCallEvaluator evaluator;
    private Scope currentScope;

    public IdaInterpreterImpl(MemorySpace memorySpace, BinaryOperationEvaluator binaryOperationEvaluator, FunctionCallEvaluator evaluator, Scope currentScope) {
        this.memorySpace = memorySpace;
        this.binaryOperationEvaluator = binaryOperationEvaluator;
        this.evaluator = evaluator;
        this.currentScope = currentScope;
        memorySpace.pushScope(currentScope);
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

        var.getGuardExpr().ifPresent(expressionNode ->{
            memorySpace.setVariable(var.getType().getName(),value);
            if(!evaluator.evaluateGuardExpression(expressionNode,this)){
                throw new RuntimeException("NIE WOLNO TAK");
            }
            memorySpace.setVariable(var.getType().getName(),null);
        });

        memorySpace.setVariable(node.getVariableName(),value);
        return null;
    }

    @Override
    public Object execute(BlockNode node) {
        memorySpace.pushScope(node.getScope());
        currentScope = node.getScope();
        Object lastValue = null;
        for (StatementNode e : node.getStatements()) {
            lastValue = e.execute(this);
        }
        memorySpace.pop();
        currentScope = node.getScope().getUpperScope();
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
        var evaluatedArgs = node.getArguments().stream().map(e->e.execute(this)).toList();
        memorySpace.pushScope(fun);
        currentScope = fun;
        Map.Entry<List<Symbol>,BlockNode> finalFun = evaluator.eval(fun,evaluatedArgs,this,memorySpace);
        if(finalFun == null) {
            throw new RuntimeException("AAAAAAAAAA");
        }
        IntStream.range(0,node.getArguments().size())
                        .mapToObj(i->new AbstractMap.SimpleEntry<>(finalFun.getKey().get(i),evaluatedArgs.get(i)))
                                .forEach(e-> memorySpace.setVariable(e.getKey().getName(),e.getValue()));
        Object ret = null;
        if(fun.getType()!=null) {
            ret = finalFun.getValue().execute(this);
        }
        memorySpace.pop();
        currentScope = fun.getUpperScope();
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
        node.getInitializer().ifPresent(e->{
            memorySpace.setVariable(node.getVariable().getName(),e.execute(this));
        });
        return null;
    }

    @Override
    public Object execute(StatementNode node) {
        return null;
    }
}

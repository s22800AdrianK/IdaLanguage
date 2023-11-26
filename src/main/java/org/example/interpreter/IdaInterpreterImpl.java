package org.example.interpreter;

import org.example.ast.*;
import org.example.ast.PrimaryExNode;
import org.example.handler.Handler;
import org.example.scope.Scope;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.StructureSymbol;
import org.example.symbol.Symbol;
import org.example.symbol.VarSymbol;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

        if(node.getTarget() instanceof DotOpNode dotOpNode) {
            assignToField(dotOpNode, value);
        } else if (node.getTarget() instanceof PrimaryExNode exNode) {
            VarSymbol var = (VarSymbol) currentScope.resolve(exNode.getValue());
            var.getGuardExpr().ifPresent(expressionNode -> expressionEvaluator.executeWithGuard(var,value,expressionNode));
            memorySpace.setVariable(node.getVariableName(),value);
        }

        return null;
    }

    private void assignToField(DotOpNode dotOpNode, Object value) {
        StructureInstance st = loadStructInstance(dotOpNode);
        if(st!=null) {
            st.getFields().put(dotOpNode.getRight().get().getValue(),value);
        }
    }

    private StructureInstance loadStructInstance(DotOpNode dotOpNode) {
        Object left = dotOpNode.getLeft().execute(this);
        if(left instanceof StructureInstance leftStruct) {
            if(leftStruct.getFields().get(dotOpNode.getRight().get().getValue()) instanceof StructureInstance rightStruct) {
                return rightStruct;
            }
            return leftStruct;
        }
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
        Class<? extends Symbol> aClass = currentScope.resolve(node.getName()).getClass();
        if (aClass.equals(FunctionSymbol.class)) {
            return processAsAFunction(node);
        }
        return processAsAStruct(node);
    }

    private Object processAsAStruct(FunctionCallNode node) {
        StructureSymbol structure = (StructureSymbol) currentScope.resolve(node.getName());
        var evaluatedArgs =functionCallEvaluator.evaluateArguments(node);
        pushScope(structure);
        var res = functionCallEvaluator.matchesArguments(structure.getConstructorArgs(),evaluatedArgs,memorySpace);
        if(!res) {
            throw new RuntimeException("SSSSSS");
        }
        //functionCallEvaluator.assignArgumentsToParameters();
        structure.getBody().execute(this);
        var structFields = popScope(structure);
        return new StructureInstance(structFields,evaluatedArgs,structure);
    }

    private Object processAsAFunction(FunctionCallNode node) {
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

    @Override
    public Object execute(StructureNode node) { return null;}

    @Override
    public Object execute(DotOpNode node) {
        Object left = node.getLeft().execute(this);
        if(left instanceof StructureInstance struct) {
            return struct.getFields().get(node.getRight().get().getValue());
        }
        return null;
    }

    private void pushScope(Scope scope) {
        memorySpace.pushScope(scope);
        currentScope = scope;
    }

    private Map<String,Object> popScope(Scope scope) {
        currentScope = scope.getUpperScope();
        return memorySpace.pop();
    }

    @Override
    public void setNextHandler(Handler handler) {}

    @Override
    public void handle(ProgramNode programNode) {
        pushScope(currentScope);
        programNode.execute(this);
    }
}

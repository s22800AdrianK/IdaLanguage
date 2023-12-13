package org.example.interpreter;

import org.example.ast.*;
import org.example.ast.PrimaryExNode;
import org.example.ast.ArrayAccessNode;
import org.example.handler.Handler;
import org.example.scope.Scope;
import org.example.symbol.*;
import org.example.symbol.builtIn.BuiltInTypeSymbol;
import org.example.token.TokenType;

import java.math.BigDecimal;
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
    public void execute(ProgramNode node) {
        node.getStatements().forEach(e->e.execute(this));
    }

    @Override
    public void execute(AssignmentNode node) {
        Object value = node.getExpression().execute(this);
        if(node.getTarget() instanceof DotOpNode dotOpNode) {
            assignToField(dotOpNode, value);
        } else if (node.getTarget() instanceof PrimaryExNode exNode) {
            VarSymbol var = (VarSymbol) currentScope.resolve(exNode.getValue());
            var.getGuardExpr().ifPresent(expressionNode -> expressionEvaluator.executeWithGuard(var,value,expressionNode));
            memorySpace.setVariable(var.getName(), value);
        } else if (node.getTarget() instanceof ArrayAccessNode arrayAccessNode) {
            var instance = (ArrayInstance) memorySpace.getVariable(arrayAccessNode.getName());
            int index = ((BigDecimal)arrayAccessNode.getIndex().execute(this)).intValue();
            instance.getValues().set(index-1,value);
        }
    }

    private void assignToField(DotOpNode dotOpNode, Object value) {
        StructureInstance st = loadStructInstance(dotOpNode);
        if(st!=null) {
            st.getFields().put(dotOpNode.getRight().get().getValue(),value);
        }
    }

    private StructureInstance loadStructInstance(DotOpNode dotOpNode) {
        StructureInstance left = (StructureInstance) dotOpNode.getLeft().execute(this);
        if(left.getFields().get(dotOpNode.getRight().get().getValue()) instanceof StructureInstance rightStruct) {
            return rightStruct;
        }
        return left;
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
    public void execute(IfStatementNode node) {
        if((boolean)node.getCondition().execute(this)) {
            node.getThenBlock().execute(this);
        }else {
            node.getElseBlock().ifPresent(e->e.execute(this));
        }
    }

    @Override
    public void execute(PrintStatementNode node) {
        System.out.println(node.getExpression().execute(this));
    }

    @Override
    public Object execute(ExpressionNode expressionNode) {
        return null;
    }

    @Override
    public Object execute(FunctionCallNode node) {
        Symbol symbol = currentScope.resolve(node.getName());
        if (symbol instanceof FunctionAggregateSymbol functionAggregateSymbol) {
            return processAsAFunction(node, functionAggregateSymbol);
        } else if (symbol instanceof StructureSymbol structureSymbol) {
            return processAsAStruct(node, structureSymbol);
        }
        throw new RuntimeException();
    }

    private Object processAsAStruct(FunctionCallNode node, StructureSymbol structureSymbol) {
        var evaluatedArgs =functionCallEvaluator.evaluateArguments(node);
        var strInst = new StructureInstance(structureSymbol);
        pushStruct(strInst);
        if(!functionCallEvaluator.matchesArguments(structureSymbol.getConstructorArgs(),evaluatedArgs,memorySpace)) {
            throw new RuntimeException("SSSSSS");
        }
        functionCallEvaluator.assignArgumentsToParameters(structureSymbol.getConstructorArgs(),evaluatedArgs);
        structureSymbol.getBody().getStatements().forEach(e->e.execute(this));
        popScope(structureSymbol);
        return strInst;
    }

    private Object processAsAFunction(FunctionCallNode node, FunctionAggregateSymbol functionAggregateSymbol) {
        var evaluatedArgs =functionCallEvaluator.evaluateArguments(node);
        FunctionSymbol finalFun = functionCallEvaluator.eval(functionAggregateSymbol,evaluatedArgs,memorySpace)
                        .orElseThrow(()->new RuntimeException("aaaa"));
        pushScope(finalFun);
        functionCallEvaluator.assignArgumentsToParameters(finalFun.getSymbols(),evaluatedArgs);
        Object ret = finalFun.getBody().execute(this);
        popScope(finalFun);
        return finalFun.getType()==null? null : ret;
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
            case STRING -> new BuiltInInstance(node.getValue(),(BuiltInTypeSymbol) currentScope.resolve(TokenType.TYPE_STRING.getRegex()));
            case BOOL -> node.getValue().equals("true");
            case NAME, THIS_KEYWORD -> memorySpace.getVariable(node.getValue());
            default -> null;
        };
    }

    @Override
    public Object execute(PrimaryGuardNode node) {
        return memorySpace.getVariable(node.getToken().getValue());
    }

    @Override
    public void execute(VariableDefNode node) {
        node.getInitializer().ifPresent(expressionNode->{
            Object value = expressionNode.execute(this);
            VarSymbol var = (VarSymbol) currentScope.resolve(node.getVariable().getName());
            var.getGuardExpr().ifPresent(guard -> expressionEvaluator.executeWithGuard(var,value,guard));
            memorySpace.setVariable(node.getVariable().getName(),value);
        });
    }

    @Override
    public void execute(WhileStatementNode node) {
        while ((boolean)node.getCondition().execute(this)) {
            node.getThenBlock().execute(this);
        }
    }

    @Override
    public Object execute(DotOpNode node) {
        Object left = node.getLeft().execute(this);
        if(left instanceof StructureInstance struct) {
            return node.getRight().isRight()?
                    struct.getFields().get(node.getRight().get().getValue())
                    : execStructFunction(struct,node.getRight().getLeft());
        }
        throw new RuntimeException("");
    }

    private Object execStructFunction(StructureInstance struct, FunctionCallNode node) {
        memorySpace.pushStruct(struct);
        currentScope = struct.getStruct();
        Object ret = node.execute(this);
        memorySpace.pop();
        currentScope = struct.getStruct().getUpperScope();
        return ret;
    }
    @Override
    public Object execute(ArrayAccessNode node) {
        var target = (ArrayInstance)node.getTarget().execute(this);
        int index = ((BigDecimal)node.getIndex().execute(this)).intValue()-1;
        return target.getValues().get(index);
    }

    @Override
    public ArrayInstance execute(ArrayNode node) {
        ArrayInstance arrInstance = new ArrayInstance(node.getSymbol());
        node.getElements().forEach(el->arrInstance.getValues().add(el.execute(this)));
        return arrInstance;
    }

    @Override
    public MemorySpace getMemorySpace() {
        return this.memorySpace;
    }

    private void pushScope(Scope scope) {
        memorySpace.pushScope(scope);
        currentScope = scope;
    }

    private void pushStruct(StructureInstance str) {
        memorySpace.pushStruct(str);
        currentScope = str.getStruct();
    }

    private void popScope(Scope scope) {
        currentScope = scope.getUpperScope();
        memorySpace.pop();
    }

    @Override
    public void setNextHandler(Handler handler) {}

    @Override
    public void handle(ProgramNode programNode) {
        pushScope(currentScope);
        programNode.execute(this);
    }
}

package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;
import org.example.exceptions.OperatorNotAllowedException;
import org.example.exceptions.WrongTypeAssignedException;
import org.example.handler.VisitorHandler;
import org.example.exceptions.ArgumentTypeMismatch;
import org.example.exceptions.ImplementationArgumentNumberException;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;
import org.example.symbol.*;
import org.example.symbol.builtIn.ArrayBuiltInTypeSymbol;
import org.example.token.TokenType;
import org.example.type.Type;
import org.example.type.TypeResolver;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpressionTypesVisitorImpl extends VisitorHandler implements ExpressionTypesVisitor {
    private final TypeResolver typeResolver;
    private Scope currentScope;

    public ExpressionTypesVisitorImpl(TypeResolver typeResolver, SymbolTable symbolTable) {
        this.typeResolver = typeResolver;
        currentScope = symbolTable;
    }
    @Override
    public void visit(AssignmentNode node) {
        node.getTarget().visit(this);
        if(node.getExpression() instanceof ArrayNode arrayNode && arrayNode.getElements().isEmpty()) {
            node.getExpression().setEvalType(node.getTarget().getEvalType());
            return;
        }
        node.getExpression().visit(this);
        if(!node.getTarget().getEvalType().equals(node.getExpression().getEvalType())) {
            throw new WrongTypeAssignedException(node.getToken().getLine());
        }
    }

    @Override
    public void visit(BinaryOpNode node) {
        ExpressionNode left = node.getLeft();
        ExpressionNode right = node.getRight();
        left.visit(this);
        right.visit(this);
        Type ret = typeResolver.getEvalType(node.getOperator(), left.getEvalType(), right.getEvalType());
        if (ret == null) {
            throw new OperatorNotAllowedException(
                    node.getOperator().getRegex(),
                    left.getEvalType().getName(),
                    right.getEvalType().getName(),
                    node.getToken().getLine());
        }
        node.setEvalType(ret);
    }

    @Override
    public void visit(BlockNode node) {
        currentScope = node.getScope();
        node.getStatements().forEach(e -> e.visit(this));
        currentScope = node.getScope().getUpperScope();
    }

    @Override
    public void visit(ExpressionNode node) {}

    @Override
    public void visit(FunctionCallNode node) {
        Symbol symbol = currentScope.resolve(node.getName());
        if (symbol instanceof FunctionAggregateSymbol functionAggregateSymbol) {
            processAsAFunction(node,functionAggregateSymbol);
        } else if (symbol instanceof StructureSymbol structureSymbol) {
            processAsAStruct(node,structureSymbol);
        }
    }

    private void processAsAStruct(FunctionCallNode node, StructureSymbol structureSymbol) {
        node.getArguments().forEach(e->e.visit(this));
        List<Symbol> args = structureSymbol.getConstructorArgs();
        if(args.size()!=node.getArguments().size()) {
            throw new RuntimeException("wrong number of constructor arguments");
        }
        boolean typesMatch = IntStream.range(0,args.size())
                .allMatch(i->args.get(i).getType().equals(node.getArguments().get(i).getEvalType()));

        if(!typesMatch) {
            throw new RuntimeException();
        }
        node.setEvalType(structureSymbol);
    }

    private void processAsAFunction(FunctionCallNode node, FunctionAggregateSymbol functionAggregateSymbol) {
        node.getArguments().forEach(e->e.visit(this));
        List<Symbol> params = functionAggregateSymbol.getFunctionSymbols().get(0).getSymbols();
        if(params.size()!=node.getArguments().size()) {
            throw new RuntimeException("wrong number of call arguments");
        }
        boolean typesMatch = IntStream.range(0,params.size())
                .allMatch(i->params.get(i).getType().equals(node.getArguments().get(i).getEvalType()));

        if(!typesMatch) {
            throw new RuntimeException();
        }

        node.setEvalType(functionAggregateSymbol.getFunctionSymbols().get(0).getType());
    }



    @Override
    public void visit(FunctionDefNode node) {
        currentScope = node.getFunctionSymbol();
        node.getReturnType().ifPresent(e->e.visit(this));
        node.getFunctionSymbol().setType(node.getReturnType().map(TypeSpecifierNode::getType).orElse(null));
        node.getParameters().forEach(e->e.visit(this));
        node.getBody().visit(this);
        if (hasWrongReturnDeclaration(node)) {
            throw new RuntimeException("Wrong return declaration");
        }

        currentScope = node.getFunctionSymbol().getUpperScope();
    }

    private boolean hasWrongReturnDeclaration(FunctionDefNode node) {
        if (node.getReturnType().isPresent()) {
            ExpressionNode lastStatement = getLastStatement(node.getBody());
            if (lastStatement != null) {
                return !lastStatement.getEvalType().equals(node.getReturnType().get().getType());
            }
        }
        return false;
    }

    private ExpressionNode getLastStatement(BlockNode blockNode) {
        List<StatementNode> statements = blockNode.getStatements();
        if (!statements.isEmpty() && statements.get(statements.size() - 1) instanceof ExpressionNode) {
            return (ExpressionNode) statements.get(statements.size() - 1);
        }
        return null;
    }

    @Override
    public void visit(IfStatementNode node) {
        node.getCondition().visit(this);
        node.getThenBlock().visit(this);
        node.getElseBlock().ifPresent(e->e.visit(this));
    }

    @Override
    public void visit(ParameterNode node) {
        currentScope.resolve(node.getName()).setType(currentScope.resolveType(node.getTypeSpecifierNode().getTypeName()));
        node.setType(currentScope.resolveType(node.getTypeSpecifierNode().getTypeName()));
        node.getGuardExpression().ifPresent(e->e.visit(this));
    }

    @Override
    public void visit(PrimaryExNode node) {
        switch (node.getToken().getType()) {
            case NUMBER -> node.setEvalType(currentScope.resolveType(TokenType.TYPE_NUMBER.getRegex()));
            case STRING -> node.setEvalType(currentScope.resolveType(TokenType.TYPE_STRING.getRegex()));
            case BOOL -> node.setEvalType(currentScope.resolveType(TokenType.TYPE_BOOL.getRegex()));
            case NAME,THIS_KEYWORD -> node.setEvalType(currentScope.resolve(node.getValue()).getType());
        }
    }

    @Override
    public void visit(PrintStatementNode node) {
        node.getExpression().visit(this);
    }

    @Override
    public void visit(ProgramNode node) {
        node.getStatements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(TypeSpecifierNode node) {
        var type = currentScope.resolveType(node.getTypeName());
        if (type == null) {
            throw new RuntimeException("unknown type: "+node.getTypeName());
        }
        Type finType = node.isArrayType()?
                IntStream.range(0,node.getArrTypeLevel())
                    .mapToObj(i->(Function<Type,Type>)this::resolveArrayType)
                    .reduce(Function.identity(),Function::andThen)
                    .apply(type)
                : type;
        node.setType(finType);
    }

    @Override
    public void visit(VariableDefNode node) {
            node.getVariable().getTypeSpecifierNode().visit(this);
            Type type = node.getVariable().getTypeSpecifierNode().getType();
            node.getVariable().setType(type);
            currentScope.resolve(node.getVariable().getName()).setType(type);
            node.getInitializer().ifPresent(e->{
                e.visit(this);
                if(!node.getVariable().getType().equals(e.getEvalType())){
                    throw new RuntimeException("type: "+e.getEvalType()+" can't be assigned to "+node.getVariable().getType().getName());
                }
            });
    }

    @Override
    public void visit(StatementNode node) {}

    @Override
    public void visit(PrimaryGuardNode node) {
        node.setEvalType(currentScope.resolveType(node.getToken().getValue()));
    }

    @Override
    public void visit(WhileStatementNode node) {
        node.getCondition().visit(this);
        node.getThenBlock().visit(this);
    }

    @Override
    public void visit(StructureNode node) {
        currentScope = node.getSymbol();
        node.getConstructorParams().forEach(p->p.visit(this));
        node.getBody().visit(this);
        currentScope = node.getSymbol().getUpperScope();
    }

    @Override
    public void visit(DotOpNode node) {
        node.getLeft().visit(this);
        StructureSymbol structureSymbol = (StructureSymbol) node.getLeft().getEvalType();
        node.getRight().peek(token -> {
            if(!structureSymbol.checkIfHasFieldOrFunction(token.getValue())) {
                throw new RuntimeException("not a field in structure");
            }
            node.setEvalType(structureSymbol.resolve(token.getValue()).getType());
        }).peekLeft(fcall -> {
            if(!structureSymbol.checkIfHasFieldOrFunction(fcall.getName())) {
                throw new RuntimeException("not a function in structure");
            }
            currentScope = structureSymbol;
            fcall.visit(this);
            currentScope = structureSymbol.getUpperScope();
            node.setEvalType(fcall.getEvalType());
        });
    }

    @Override
    public void visit(ArrayNode node) {
        node.getElements().forEach(e->e.visit(this));
        boolean isOneTypeArray = node.getElements()
                .stream()
                .map(ExpressionNode::getEvalType)
                .distinct()
                .limit(2)
                .count() == 1;
        if(!isOneTypeArray) {
            throw new RuntimeException(node.getToken().getValue()+" is not oneTypeArray");
        }
        ArrayBuiltInTypeSymbol arrayBuiltInTypeSymbol = resolveArrayType(node.getElements().get(0).getEvalType());
        node.setSymbol(arrayBuiltInTypeSymbol);
        node.setEvalType(arrayBuiltInTypeSymbol);
    }

    @Override
    public void visit(ArrayAccessNode node) {
        node.getTarget().visit(this);
        node.getIndex().visit(this);
        if(!node.getIndex().getEvalType().equals(currentScope.resolveType(TokenType.TYPE_NUMBER.getRegex()))) {
            throw new RuntimeException();
        }
        node.setEvalType(node.getTarget().getEvalType());
    }

    private ArrayBuiltInTypeSymbol resolveArrayType(Type baseType) {
        var arrType = ArrayBuiltInTypeSymbol.of(baseType,currentScope);
        var type = (ArrayBuiltInTypeSymbol)currentScope.resolveType(arrType.getName());
        if(type==null) {
            currentScope.defineSymbol(arrType);
            return arrType;
        }
        return type;
    }
}

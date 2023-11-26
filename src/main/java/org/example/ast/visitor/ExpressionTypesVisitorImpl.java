package org.example.ast.visitor;

import org.example.ast.*;
import org.example.ast.BinaryOpNode;
import org.example.ast.PrimaryExNode;
import org.example.exceptions.NotAFunctionException;
import org.example.handler.VisitorHandler;
import org.example.exceptions.ArgumentTypeMismatch;
import org.example.exceptions.ImplementationArgumentNumberException;
import org.example.scope.Scope;
import org.example.scope.SymbolTable;
import org.example.symbol.FunctionSymbol;
import org.example.symbol.StructureSymbol;
import org.example.symbol.Symbol;
import org.example.token.TokenType;
import org.example.type.Type;
import org.example.type.TypeResolver;

import java.util.List;
import java.util.Set;
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
        node.getExpression().visit(this);
    }

    @Override
    public void visit(BinaryOpNode node) {
        ExpressionNode left = node.getLeft();
        ExpressionNode right = node.getRight();
        left.visit(this);
        right.visit(this);
        Type ret = typeResolver.getEvalType(node.getOperator(), left.getEvalType(), right.getEvalType());
        if (ret == null) {
            throw new RuntimeException(
                    "operator:" + node.getOperator() + "not allowed for:" + left.getEvalType().getName() + " and " + right.getEvalType().getName()
            );
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
        Class<? extends Symbol> aClass = currentScope.resolve(node.getName()).getClass();
        if (aClass.equals(FunctionSymbol.class)) {
            processAsAFunction(node);
        } else if (aClass.equals(StructureSymbol.class)) {
            processAsAStruct(node);
        }
    }

    private void processAsAStruct(FunctionCallNode node) {
        StructureSymbol st = (StructureSymbol) currentScope.resolve(node.getName());
        node.getArguments().forEach(e->e.visit(this));
        List<Symbol> args = st.getConstructorArgs();
        if(args.size()!=node.getArguments().size()) {
            throw new RuntimeException("wrong number of constructor arguments");
        }
        boolean typesMatch = IntStream.range(0,args.size())
                .allMatch(i->args.get(i).getType().equals(node.getArguments().get(i).getEvalType()));

        if(!typesMatch) {
            throw new RuntimeException();
        }
        node.setEvalType(st);
    }

    private void processAsAFunction(FunctionCallNode node) {
        FunctionSymbol fn = (FunctionSymbol) currentScope.resolve(node.getName());
        node.getArguments().forEach(e->e.visit(this));

        List<Symbol> params = fn.getImplementations().keySet().stream().toList().get(0);
        if(params.size()!=node.getArguments().size()) {
            throw new RuntimeException("wrong number of call arguments");
        }
        boolean typesMatch = IntStream.range(0,params.size())
                .allMatch(i->params.get(i).getType().equals(node.getArguments().get(i).getEvalType()));

        if(!typesMatch) {
            throw new RuntimeException();
        }

        node.setEvalType(fn.getType());
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

        var implementations = node.getFunctionSymbol().getImplementations();

        Set<Integer> sizes = implementations.keySet().stream()
                .map(List::size)
                .collect(Collectors.toSet());

        if(sizes.size()!=1) {
            throw new ImplementationArgumentNumberException(node.getToken().getValue());
        }

        boolean typesMatch = IntStream.range(0,sizes.size())
                .allMatch(i-> implementations.keySet()
                                    .stream()
                                    .filter(list -> list.size() > i)
                                    .map(list->list.get(i).getType())
                                    .distinct()
                                    .limit(2)
                                    .count()<=1);

        if(!typesMatch) {
            throw new ArgumentTypeMismatch(node.getToken().getValue());
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
        node.setTypes(currentScope.resolveType(node.getTypeSpecifierNode().getTypeName()));
        node.getGuardExpression().ifPresent(e->e.visit(this));
    }

    @Override
    public void visit(PrimaryExNode node) {
        switch (node.getToken().getType()) {
            case NUMBER -> node.setEvalType(currentScope.resolveType(TokenType.TYPE_NUMBER.getRegex()));
            case STRING -> node.setEvalType(currentScope.resolveType(TokenType.TYPE_STRING.getRegex()));
            case BOOL -> node.setEvalType(currentScope.resolveType(TokenType.TYPE_BOOL.getRegex()));
            case NAME -> node.setEvalType(currentScope.resolve(node.getValue()).getType());
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
        node.setType(currentScope.resolveType(node.getTypeName()));
    }

    @Override
    public void visit(VariableDefNode node) {
            Type type = currentScope.resolveType(node.getVariable().getTypeSpecifierNode().getTypeName());
            if (type == null) {
                throw new RuntimeException("unknown type");
            }
            node.getVariable().setTypes(type);
            currentScope.resolve(node.getVariable().getName()).setType(type);
            node.getInitializer().ifPresent(e->{
                e.visit(this);
                if(!node.getVariable().getTypes().equals(e.getEvalType())){
                    throw new RuntimeException("type: "+e.getEvalType()+" can't be assigned to "+node.getVariable().getTypes().getName());
                }
            });
            node.getVariable().visit(this);
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
        }).peekLeft(fcall -> {
            if(!structureSymbol.checkIfHasFieldOrFunction(fcall.getName())) {
                throw new RuntimeException("not a function in structure");
            }
        });
    }

}

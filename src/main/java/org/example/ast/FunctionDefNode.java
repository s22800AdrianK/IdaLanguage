package org.example.ast;

import org.example.ast.visitor.Visitor;
import org.example.interpreter.IdaInterpreter;
import org.example.symbol.FunctionSymbol;
import org.example.token.Token;

import java.util.List;
import java.util.Optional;

public class FunctionDefNode extends StatementNode {
    private List<ParameterNode> parameters;
    private TypeSpecifierNode returnType;
    private BlockNode body;
    private FunctionSymbol functionSymbol;

    public FunctionDefNode(Token token) {
        super(token);
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterNode> parameters) {
        this.parameters = parameters;
    }

    public Optional<TypeSpecifierNode> getReturnType() {
        return Optional.ofNullable(returnType);
    }

    public void setReturnType(TypeSpecifierNode returnType) {
        this.returnType = returnType;
    }

    public BlockNode getBody() {
        return body;
    }

    public void setBody(BlockNode body) {
        this.body = body;
    }

    public FunctionSymbol getFunctionSymbol() {
        return functionSymbol;
    }

    public void setFunctionSymbol(FunctionSymbol functionSymbol) {
        this.functionSymbol = functionSymbol;
    }
    @Override
    public void visit(Visitor visitor) {
        visitor.visit(this);
    }
    @Override
    public Object execute(IdaInterpreter interpreter) {
        return interpreter.execute(this);
    }
}

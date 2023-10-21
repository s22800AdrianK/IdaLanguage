package org.example.ast;

import org.example.token.Token;

import java.util.List;

public class FunctionDefNode extends StatementNode{
    private List<ParameterNode> parameters;
    private TypeSpecifierNode returnType;
    private BlockNode body;
    public FunctionDefNode(Token token) {
        super(token);
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterNode> parameters) {
        this.parameters = parameters;
    }

    public TypeSpecifierNode getReturnType() {
        return returnType;
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
}

package org.example.exceptions;

public class FunctionAlreadyDefinedException extends IdaException {
    private static final String format = "Function : %s already has been defined with different return type";
    public FunctionAlreadyDefinedException(String funName, int line) {
        super(String.format(format,funName), line);
    }
}

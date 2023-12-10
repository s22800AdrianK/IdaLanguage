package org.example.exceptions;

public class ImplementationArgumentNumberException extends IdaException {

    public ImplementationArgumentNumberException(String funcName,int line) {
        super("Function: '"+funcName+"' has different number of arguments in implementations", line);
    }
}

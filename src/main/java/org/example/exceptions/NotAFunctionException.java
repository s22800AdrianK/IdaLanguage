package org.example.exceptions;

public class NotAFunctionException extends IdaException {
    public NotAFunctionException(String name,int line){
        super("'"+name+"' is not a function and can't be used with call operator",line);
    }
}

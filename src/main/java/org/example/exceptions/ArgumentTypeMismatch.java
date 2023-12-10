package org.example.exceptions;

public class ArgumentTypeMismatch extends IdaException {
    public ArgumentTypeMismatch(String funcName,int line){
        super("Function: '"+funcName+"' has type mismatch in arguments ",line);
    }
}

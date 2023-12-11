package org.example.exceptions;

public class OperatorNotAllowedException extends IdaException{

    private static final String format = "operator: %s not allowed for: %s and %s";
    public OperatorNotAllowedException(String operator,String lType, String rType, int line) {
        super(String.format(format,operator,lType,rType), line);
    }
}

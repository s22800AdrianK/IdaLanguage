package org.example.exceptions;

public class WrongTypeAssignedException extends IdaException{

    private static final String message = "Type mismatch between L-expression and R-expression";
    public WrongTypeAssignedException(int line) {
        super(message, line);
    }
}

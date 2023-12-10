package org.example.exceptions;

public class VariableNotDefinedException extends IdaException {

    public VariableNotDefinedException(String varName, int line) {
        super("Variable: '"+varName+"' wasn't defined before usage",line);
    }
}

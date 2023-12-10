package org.example.exceptions;

public class VariableAlreadyDefinedException extends IdaException {
    public VariableAlreadyDefinedException(String varname, int line) {
        super("Variable: '" +varname+"' is already defined",line);
    }
}

package org.example.exceptions;

public class VariableAlreadyDefinedException extends RuntimeException{
    public VariableAlreadyDefinedException(String varname) {
        super(
                "Variable: "+varname+"is already defined"
        );
    }
}

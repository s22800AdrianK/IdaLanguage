package org.example.exceptions;

public class VariableNotDefinedException extends RuntimeException{

    public VariableNotDefinedException(String varName) {
        super(
                "Variable: "+varName+"wasn't defined before usage"
        );
    }
}

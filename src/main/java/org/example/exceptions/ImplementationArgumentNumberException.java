package org.example.exceptions;

public class ImplementationArgumentNumberException extends RuntimeException {

    public ImplementationArgumentNumberException(String funcName) {
        super(
                "Function: "+funcName+" has different number of arguments in implementations"
        );
    }
}

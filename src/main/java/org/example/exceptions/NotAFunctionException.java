package org.example.exceptions;

public class NotAFunctionException extends RuntimeException{

    public NotAFunctionException(String name){
        super(
                name+"is not a function and can't be used with call operator"
        );
    }
}

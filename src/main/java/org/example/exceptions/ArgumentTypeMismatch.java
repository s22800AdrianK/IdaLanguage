package org.example.exceptions;

public class ArgumentTypeMismatch extends RuntimeException{
    public ArgumentTypeMismatch(String funcName){
        super(
                "Function: "+funcName+" has type mismatch in arguments "
        );
    }
}

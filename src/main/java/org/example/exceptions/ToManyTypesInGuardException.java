package org.example.exceptions;

import org.example.type.Type;

import java.util.List;

public class ToManyTypesInGuardException extends RuntimeException{

    public ToManyTypesInGuardException(String varName){
        super(
                "To many types declared for variable:"+varName+" inside guard expression. There can be only one"
        );
    }
}

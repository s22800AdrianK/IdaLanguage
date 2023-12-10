package org.example.exceptions;

public class ToManyTypesInGuardException extends IdaException {
    public ToManyTypesInGuardException(String varName, int line){
        super(
                "To many types declared for variable '"+varName+"' inside guard expression. There can be only one",
                line
        );
    }
}

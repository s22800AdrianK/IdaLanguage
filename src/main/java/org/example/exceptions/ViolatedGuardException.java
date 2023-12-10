package org.example.exceptions;

public class ViolatedGuardException extends IdaException{

    public ViolatedGuardException(String varName, int line) {
        super("Variable: '"+varName+"' attempted to be assigned a value not allowed by guard", line);
    }
}

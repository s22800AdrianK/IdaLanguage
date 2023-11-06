package org.example.exceptions;

public class TypeNotDefinedException extends RuntimeException{
    public TypeNotDefinedException(String typeName) {
        super(
                "Type: "+typeName+"wasn't defined before usage"
        );
    }
}

package org.example.exceptions;

public class TypeNotDefinedException extends IdaException {
    public TypeNotDefinedException(String typeName, int line) {
        super("Type: "+typeName+"wasn't defined before usage",line);
    }
}

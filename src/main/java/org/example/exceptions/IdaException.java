package org.example.exceptions;

public class IdaException extends RuntimeException{
    private final int line;
    public IdaException(String message, int line) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}

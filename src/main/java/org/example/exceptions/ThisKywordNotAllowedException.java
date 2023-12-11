package org.example.exceptions;

public class ThisKywordNotAllowedException extends IdaException {
    private static final String message = "this keyward is only allowed in structures";
    public ThisKywordNotAllowedException(int line) {
        super(message, line);
    }
}

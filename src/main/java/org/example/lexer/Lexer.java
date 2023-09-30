package org.example.lexer;

import org.example.token.Token;

public abstract class Lexer {
    private static boolean isEmpty = false;
    private final String input;
    private int positionOfCurrent = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public String getNext() {
        return isEmpty()? null : input.substring(positionOfCurrent);
    }
    public abstract Token nextToken();

    public static boolean isEmpty() {
        return isEmpty;
    }

    public String getInput() {
        return input;
    }

    public int getPositionOfCurrent() {
        return positionOfCurrent;
    }


    public void advance(int moveIndex) {
        positionOfCurrent+= moveIndex;
        if (positionOfCurrent >= input.length()) {
            isEmpty = true;
        }
    }
}

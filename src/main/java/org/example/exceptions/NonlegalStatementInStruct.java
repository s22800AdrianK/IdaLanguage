package org.example.exceptions;

public class NonlegalStatementInStruct extends RuntimeException {

    public NonlegalStatementInStruct(String structName) {
        super("Nonlegal statement in struct: "+structName+" only variable definition allowed");
    }
}

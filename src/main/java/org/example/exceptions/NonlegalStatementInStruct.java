package org.example.exceptions;

public class NonlegalStatementInStruct extends IdaException {

    public NonlegalStatementInStruct(String structName,int line) {
        super("Nonlegal statement in struct: '"+structName+"' only variable definition allowed",line);
    }
}

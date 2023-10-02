package org.example.lexer

import spock.lang.Specification
import org.example.token.TokenType

class IdaLexerSpec extends Specification {

    def "test nextToken with input #input"() {
        given:
            def idaLexer = new IdaLexer(input)
        expect:
            def token = idaLexer.nextToken()
            token.getType() == expectedType
            token.getValue() == expectedValue
        where:
        input          || expectedType       || expectedValue
        "someName"     || TokenType.NAME     || "someName"
        "123"          || TokenType.INT      || "123"
        "123.456"      || TokenType.FLOAT    || "123.456"
        "+"            || TokenType.ADD      || "+"
        "fn"           || TokenType.FN       || "fn"
        "\"ala\""      || TokenType.STRING   || "ala"
    }

    def "test nextToken with unexpected sign"() {
        given:
            def input = "#"
            def idaLexer = new IdaLexer(input)
        when:
            idaLexer.nextToken()
        then:
            def err = thrown(Exception)
            err.message == "unexpected sign at position: 0 #"
    }
}

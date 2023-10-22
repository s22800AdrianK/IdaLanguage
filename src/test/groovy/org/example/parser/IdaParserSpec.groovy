package org.example.parser

import org.example.ast.FunctionDefNode
import org.example.ast.IfStatementNode
import org.example.ast.ProgramNode
import org.example.lexer.IdaLexer
import org.example.token.Token
import spock.lang.Specification
import org.example.token.TokenType

class IdaParserSpec extends Specification {
    def "should parse simple if statement"() {
        given:
        IdaLexer lexer = Mock(IdaLexer)
        lexer.nextToken() >> new Token(TokenType.IF, "if") >> new Token(TokenType.NUMBER, "12")
                >> new Token(TokenType.OP_EQUALS, "==") >> new Token(TokenType.NUMBER, "13")
                >> new Token(TokenType.L_C_BRACK, "{") >> new Token(TokenType.NAME, "a:num")
                >> new Token(TokenType.EQUALS, "=") >> new Token(TokenType.NUMBER, "13")
                >> new Token(TokenType.R_C_BRACK, "}") >> new Token(TokenType.EOF_TYPE, "<EOF>")

        IdaParser parser = new IdaParser(lexer, 10)

        when:
        ProgramNode result = parser.program()

        then:
        result.statements.size() == 1
        result.statements[0] instanceof IfStatementNode
    }

    def "should parse simple function declaration"() {
        given:
        IdaLexer lexer = Mock(IdaLexer)
        lexer.nextToken() >> new Token(TokenType.FN, "fn") >> new Token(TokenType.NAME, "func")
                >> new Token(TokenType.L_BRACK, "(") >> new Token(TokenType.R_BRACK, ")")
                >> new Token(TokenType.COLON, ":") >> new Token(TokenType.TYPE_NUMBER, "num")
                >> new Token(TokenType.L_C_BRACK, "{") >> new Token(TokenType.NAME, "a")
                >> new Token(TokenType.MULT, "*") >> new Token(TokenType.NUMBER, "2")
                >> new Token(TokenType.R_C_BRACK, "}") >> new Token(TokenType.EOF_TYPE, "<EOF>")

        IdaParser parser = new IdaParser(lexer, 10)

        when:
        ProgramNode result = parser.program()

        then:
        result.statements.size() == 1
        result.statements[0] instanceof FunctionDefNode
    }
}

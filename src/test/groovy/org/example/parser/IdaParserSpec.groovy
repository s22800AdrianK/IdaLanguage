package org.example.parser

import org.example.ast.IfStatementNode
import org.example.ast.ProgramNode
import org.example.lexer.IdaLexer
import org.example.token.Token
import spock.lang.Specification
class IdaParserSpec extends Specification {
    def "should parse simple program"() {
        given: "A lexer with a simple input"
        IdaLexer lexer = Mock(IdaLexer)
        lexer.nextToken() >> new Token(TokenType.IF, "if") >> new Token(TokenType.NUMBER, "12")
                >> new Token(TokenType.OP_EQUALS, "==") >> new Token(TokenType.NUMBER, "13")
                >> new Token(TokenType.L_C_BRACK, "{") >> new Token(TokenType.NAME, "a:num")
                >> new Token(TokenType.EQUALS, "=") >> new Token(TokenType.NUMBER, "13")
                >> new Token(TokenType.R_C_BRACK, "}") >> new Token(TokenType.EOF_TYPE, "<EOF>")

        IdaParser parser = new IdaParser(lexer, 10)

        when: "parse the program"
        ProgramNode result = parser.program()

        then: "The resulting ProgramNode contains valid statements"
        result.statements.size() == 1
        result.statements[0] instanceof IfStatementNode
    }
}

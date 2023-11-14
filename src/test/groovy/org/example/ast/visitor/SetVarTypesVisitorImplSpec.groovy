package org.example.ast.visitor

import org.example.ast.BinaryOpNode
import org.example.ast.ParameterNode
import org.example.ast.PrimaryGuardNode
import org.example.ast.ProgramNode
import org.example.ast.VariableDefNode
import org.example.ast.PrimaryExNode
import org.example.scope.SymbolTable
import org.example.symbol.BuiltInTypeSymbol
import org.example.token.Token
import org.example.token.TokenType
import spock.lang.Specification

class SetVarTypesVisitorImplSpec extends Specification {

    def "should visit nodes and set type for variable"() {
        given:
        SymbolTable symbolTable = Mock(SymbolTable)
        symbolTable.getBuliInTypeForName(_ as String) >> new BuiltInTypeSymbol("num")
        ValidateASTVisitorImpl typesVisitor = new ValidateASTVisitorImpl(symbolTable)
        ProgramNode programNode = new ProgramNode()
        VariableDefNode defNode = new  VariableDefNode(new Token(TokenType.NAME,"a")).with {
            it.setVariable(
                    new ParameterNode(
                            new Token(TokenType.NAME,"a")
                    ).with {
                        it.setGuardExpression(
                                new BinaryOpNode(new Token(TokenType.OP_GRATER,">")).with {
                                    it.setLeft(new PrimaryGuardNode(new Token(TokenType.TYPE_NUMBER,"num")))
                                    it.setRight(new PrimaryExNode(new Token(TokenType.NUMBER,"12")))
                                    return it
                                }
                        )
                        return it
                    }
            )
            return it
        }
        programNode.addStatements(defNode)
        when:
            typesVisitor.visit(programNode)
        then:
            noExceptionThrown()
            defNode.getVariable().getTypes().getName() == "num"
    }

}

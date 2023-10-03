package org.example.lexer;

import org.example.token.Token;
import org.example.token.TokenType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdaLexer extends Lexer{

    public IdaLexer(String input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        if(isEmpty()) {
            return new Token(TokenType.EOF_TYPE,"<EOF>");
        }

        for (TokenType token: TokenType.values()) {
            Pattern pattern = Pattern.compile("^(" + token.getRegex() + ")");
            Matcher matcher = pattern.matcher(getNext());
            if(token!=TokenType.WHITESPACE && matcher.find()) {
                String match = matcher.group();
                advance(match.length());
                return  createToken(match,token);
            }
        }
        throw new RuntimeException("unexpected sign at position: "+getPositionOfCurrent()+" "+getInput().charAt(getPositionOfCurrent()));
    }

    private Token createToken(String match,TokenType tokenType){
        return tokenType!=TokenType.STRING?
                new Token(tokenType,match) :
                new Token(tokenType,match.substring(1,match.length()-1).replace("\\\"","\""));

    }
}

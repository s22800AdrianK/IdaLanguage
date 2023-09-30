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
            if(matcher.find()) {
                String match = matcher.group();
                advance(match.length());
                return new Token(token,match);
            }
        }
        throw new RuntimeException("unexpected sign at position: "+getPositionOfCurrent()+" "+getInput().charAt(getPositionOfCurrent()));
    }
}

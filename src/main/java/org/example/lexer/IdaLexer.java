package org.example.lexer;

import org.example.token.Token;
import org.example.token.TokenType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IdaLexer extends Lexer {
    private int line = 1;
    private final Map<Pattern,TokenType> tokenPatterns;
    private final Map<Pattern,TokenType> nameLikePatterns;
    public IdaLexer(String input) {
        super(input);
        tokenPatterns = Arrays
                .stream(TokenType.values())
                .filter(t->!TokenType.nameLikeTokens.contains(t))
                .collect(Collectors.toMap(t->Pattern.compile("^("+t.getRegex()+")"),t->t,(x,y)->y, LinkedHashMap::new));
        nameLikePatterns = TokenType.nameLikeTokens
                .stream()
                .collect(Collectors.toMap(t->Pattern.compile("^("+t.getRegex()+")$"),t->t,(x,y)->y, LinkedHashMap::new));
    }

    @Override
    public Token nextToken() {
        if (isEmpty()) {
            return new Token(TokenType.EOF_TYPE, "<EOF>",line);
        }

        for (var entry : tokenPatterns.entrySet()) {
            Matcher matcher = entry.getKey().matcher(getNext());
            if(matcher.find()) {
                String match = matcher.group();
                advance(match.length());
                if (entry.getValue() != TokenType.WHITESPACE) {
                    return createToken(match, entry.getValue());
                }else {
                    line += (int) match.chars().filter(ch->ch=='\n').count();
                }
            }
        }
        throw new RuntimeException("unexpected sign at position: " + getPositionOfCurrent() + "," + getInput().charAt(getPositionOfCurrent()));
    }

    private Token createToken(String match, TokenType tokenType) {
        return switch (tokenType) {
            case STRING -> new Token(tokenType, match.substring(1,match.length()-1).replace("\\\"", "\""), line);
            case NAME -> processName(match);
            default -> new Token(tokenType, match,line);
        };
    }

    private Token processName(String match) {
        for(var entry : nameLikePatterns.entrySet()) {
            Matcher matcher = entry.getKey().matcher(match);
            if(matcher.find()){
                return new Token(entry.getValue(),match,line);
            }
        }
        return new Token(TokenType.NAME,match,line);
    }
}

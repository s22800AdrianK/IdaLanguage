package org.example.interpreter;

import org.example.ast.ExpressionNode;
import org.example.scope.SymbolTable;
import org.example.token.TokenType;
import org.example.type.Type;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BinaryOperationEvaluator {
    private final Map<TokenType, Map<Type, BiFunction<Object,Object,Object>>> map = new HashMap<>();
    public BinaryOperationEvaluator(SymbolTable table) {
        Type num = table.resolveType(TokenType.TYPE_NUMBER.getRegex());
        Type str = table.resolveType(TokenType.TYPE_STRING.getRegex());
        Type bool = table.resolveType(TokenType.TYPE_BOOL.getRegex());

        putEval(TokenType.ADD,num,(a,b)->{
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.add(b1);
        });

        putEval(TokenType.ADD,str,(a,b)->a.toString()+b.toString());

        putEval(TokenType.MINUS,num,(a,b)->{
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.subtract(b1);
        });

        putEval(TokenType.MULT, num, (a, b) -> {
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.multiply(b1);
        });

        putEval(TokenType.DEVID, num, (a, b) -> {
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.divide(b1, MathContext.DECIMAL128);
        });

        putEval(TokenType.MODULO, num, (a, b) -> {
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.remainder(b1);
        });

        putEval(TokenType.OP_EQUALS, num, Object::equals);
        putEval(TokenType.OP_EQUALS, str, Object::equals);
        putEval(TokenType.OP_EQUALS, bool, Object::equals);

        putEval(TokenType.OP_NOT_EQUALS, num, (a, b) -> !a.equals(b));
        putEval(TokenType.OP_NOT_EQUALS, str, (a, b) -> !a.equals(b));
        putEval(TokenType.OP_NOT_EQUALS, bool, (a, b) -> !a.equals(b));

        putEval(TokenType.OP_SMALLER,bool,(a,b)->{
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.doubleValue()<b1.doubleValue();
        });
        putEval(TokenType.OP_GRATER,bool,(a,b)->{
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            boolean ret = a1.doubleValue()>b1.doubleValue();
            return ret;
        });
        putEval(TokenType.OP_GRATER_EQUAL,bool,(a,b)->{
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.doubleValue()>=b1.doubleValue();
        });
        putEval(TokenType.OP_SMALLER_EQUAL,bool,(a,b)->{
            BigDecimal a1 = new BigDecimal(a.toString());
            BigDecimal b1 = new BigDecimal(b.toString());
            return a1.doubleValue()<=b1.doubleValue();
        });

        putEval(TokenType.OR,bool,(a,b)-> (boolean) a || (boolean) b);
        putEval(TokenType.AND,bool,(a,b)-> (boolean) a && (boolean) b);
    }

    public void putEval(TokenType operator,Type type,BiFunction<Object,Object,Object> fun){
        validateTokenType(operator);
        map.computeIfAbsent(operator,type1 -> new HashMap<>())
                .put(type,fun);
    }

    public Object evaluateExpr(TokenType operator,Type type,Object left,Object right) {
        return map.getOrDefault(operator,new HashMap<>())
                    .getOrDefault(type,(a,b)->null).apply(left,right);
    }

    public void validateTokenType(TokenType tokenType){
        if(!TokenType.operators.contains(tokenType)) {
            throw new RuntimeException("passed toke is not a operator:"+tokenType);
        }
    }
}

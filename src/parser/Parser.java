package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.logicnode.*;

import java.util.List;

public class Parser {
    private List<Token> tokens;
    private Token current;
    private int i;

    public LogicNode parse(List<Token> tokens) {
        this.tokens = tokens;
        i = 0;
        advance();

        return formula();
    }

    private LogicNode formula() {
        return implication();
    }

    private LogicNode implication() {
        LogicNode result = doubleImplication();

        while(isType(TokenType.IMPLICATION)) {
            advance();
            result = new ImplicationNode(result, doubleImplication());
        }

        return result;
    }

    private LogicNode doubleImplication() {
        LogicNode result = expression();

        while(isType(TokenType.DOUBLE_IMPLICATION)) {
            advance();
            result = new DoubleImplicationNode(result, expression());
        }

        return result;
    }

    private LogicNode expression() {
        LogicNode result = term();

        while(isType(TokenType.OR)) {
            advance();
            result = new OrNode(result, term());
        }

        return result;
    }

    private LogicNode term() {
        LogicNode result = factor();

        while(isType(TokenType.AND)) {
            advance();
            result = new AndNode(result, factor());
        }

        return result;
    }

    private LogicNode factor() {
        if(isType(TokenType.NOT)) {
            advance();
            return new NegationNode(factor());
        }

        return atom();
    }

    private LogicNode atom() {
        LogicNode result;

        switch(current.type) {
            case ACTION -> result = new ActionNode(current.value);
            case CONSTANT -> result = new ConstantNode(current.value.equals("1") || current.value.equals("true"));
            case LPAREN -> {
                advance();
                result = formula();
                assert(isType(TokenType.RPAREN));
            }
            default -> throw new IllegalArgumentException("Illegal TokenType " + current.type);
        }

        advance();
        return result;
    }

    /* H E L P E R S */

    private void advance() {
        if(i < tokens.size()) current = tokens.get(i++);
    }

    private boolean isType(TokenType type) {
        return current.type == type;
    }
}

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
        return expression();
    }

    private LogicNode expression() {
        if(isType(TokenType.EXISTS) || isType(TokenType.FOR_ALL)) return path();
        else return logic();
    }

    private LogicNode path() {
        PathQuantifier pq = isType(TokenType.EXISTS) ? PathQuantifier.E : PathQuantifier.A;
        advance();

        return switch(current.type) {
            case IMMEDIATE -> {
                advance();
                yield new ImmediateNode(pq, formula());
            }
            case FINALLY -> {
                advance();
                yield new FinallyNode(pq, formula());
            }
            case GLOBALLY -> {
                advance();
                yield new GloballyNode(pq, formula());
            }
            default -> {
                LogicNode left = formula();
                check(TokenType.UNTIL);
                yield new UntilNode(pq, left, formula());
            }
        };
    }

    private LogicNode logic() {
        LogicNode result = implication();

        while(isType(TokenType.DOUBLE_IMPLICATION)) {
            advance();
            result = new DoubleImplicationNode(result, implication());
        }

        return result;
    }

    private LogicNode implication() {
        LogicNode result = or();

        while(isType(TokenType.IMPLICATION)) {
            advance();
            result = new ImplicationNode(result, or());
        }

        return result;
    }

    private LogicNode or() {
        LogicNode result = and();

        while(isType(TokenType.OR)) {
            advance();
            result = new OrNode(result, and());
        }

        return result;
    }

    private LogicNode and() {
        LogicNode result = not();

        while(isType(TokenType.AND)) {
            advance();
            result = new AndNode(result, not());
        }

        return result;
    }

    private LogicNode not() {
        if(isType(TokenType.NOT)) {
            advance();
            return new NegationNode(atom());
        }

        return atom();
    }

    private LogicNode atom() {
        return switch(current.type) {
            case ACTION -> {
                LogicNode action = new ActionNode(current.value);
                advance();
                yield action;
            }
            case CONSTANT -> {
                LogicNode constant = new ConstantNode(current.value.equals("1") || current.value.equals("true"));
                advance();
                yield constant;
            }
            case LPAREN -> {
                advance();
                LogicNode formula = formula();
                check(TokenType.RPAREN);
                yield formula;
            }
            case default -> formula();
        };
    }

    /* H E L P E R S */

    private void advance() {
        if(i < tokens.size()) current = tokens.get(i++);
    }

    private boolean isType(TokenType type) {
        return current.type == type;
    }

    private void check(TokenType type) {
        assert(isType(type));
        advance();
    }
}

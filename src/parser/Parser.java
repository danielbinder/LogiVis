package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.node.*;

import java.util.List;

public class Parser {
    private List<Token> tokens;
    private Token current;
    private int i;

    public Node parse(List<Token> tokens) {
        this.tokens = tokens;
        i = 0;
        advance();

        return formula();
    }

    private Node formula() {
        if(isType(TokenType.LPAREN)) {
            advance();
            Node expr = expression();
            check(TokenType.RPAREN);

            return expr;
        }

        return expression();
    }

    private Node expression() {
        if(isType(TokenType.EXISTS) || isType(TokenType.FOR_ALL)) return path();
        else return logic();
    }

    private Node path() {
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
                Node left = formula();
                check(TokenType.UNTIL);
                yield new UntilNode(pq, left, formula());
            }
        };
    }

    private Node logic() {
        Node result = implication();

        while(isType(TokenType.DOUBLE_IMPLICATION)) {
            advance();
            result = new DoubleImplicationNode(result, implication());
        }

        return result;
    }

    private Node implication() {
        Node result = or();

        while(isType(TokenType.IMPLICATION)) {
            advance();
            result = new ImplicationNode(result, or());
        }

        return result;
    }

    private Node or() {
        Node result = and();

        while(isType(TokenType.OR)) {
            advance();
            result = new OrNode(result, and());
        }

        return result;
    }

    private Node and() {
        Node result = not();

        while(isType(TokenType.AND)) {
            advance();
            result = new AndNode(result, not());
        }

        return result;
    }

    private Node not() {
        if(isType(TokenType.NOT)) {
            advance();
            return new NegationNode(atom());
        }

        return atom();
    }

    private Node atom() {
        return switch(current.type) {
            case ACTION -> {
                Node action = new ActionNode(current.value);
                advance();
                yield action;
            }
            case CONSTANT -> {
                Node constant = new ConstantNode(current.value.equals("1"));
                advance();
                yield constant;
            }
            case LPAREN -> {
                advance();
                Node formula = formula();
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

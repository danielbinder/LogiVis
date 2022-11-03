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

        return formula();
    }

    private Node formula() {
        if(isType(TokenType.LPAREN)) {
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

        return switch(current.type) {
            case IMMEDIATE -> new ImmediateNode(pq, formula());
            case FINALLY -> new FinallyNode(pq, formula());
            case GLOBALLY -> new GloballyNode(pq, formula());
            default -> new UntilNode(pq, formula(), formula());     // TODO: read until type
        };
    }

    private Node logic() {
        return null;
    }

    /* H E L P E R S */

    public void advance() {
        current = tokens.get(i++);
    }

    public boolean isType(TokenType type) {
        return current.type == type;
    }

    public void check(TokenType type) {
        assert(isType(type));
    }
}

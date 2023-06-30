package bool.parser;

import bool.parser.logicnode.*;
import lexer.Lexer;
import bool.token.BooleanToken;
import bool.token.BooleanTokenType;
import marker.Parser;

import java.util.List;

public class BooleanParser implements Parser {
    private List<BooleanToken> booleanTokens;
    private BooleanToken current;
    private int i;

    public LogicNode parse(String input) {
        return parse(Lexer.tokenizeBooleanFormula(input));
    }

    public LogicNode parse(List<BooleanToken> booleanTokens) {
        this.booleanTokens = booleanTokens;
        i = 0;
        advance();

        return formula();
    }

    private LogicNode formula() {
        return doubleImplication();
    }

    /**
     * DoubleImplication = Implication ('<->' Implication)*
     */
    private LogicNode doubleImplication() {
        LogicNode result = implication();

        while(isType(BooleanTokenType.DOUBLE_IMPLICATION)) {
            advance();
            result = new DoubleImplicationNode(result, implication());
        }

        return result;
    }

    /**
     * Implication = Expression ('->' Expression)*
     */
    private LogicNode implication() {
        LogicNode result = expression();

        while(isType(BooleanTokenType.IMPLICATION)) {
            advance();
            result = new ImplicationNode(result, expression());
        }

        return result;
    }

    /**
     * Expression = Term ('|' Term)*
     */
    private LogicNode expression() {
        LogicNode result = term();

        while(isType(BooleanTokenType.OR)) {
            advance();
            result = new OrNode(result, term());
        }

        return result;
    }

    /**
     * Term = Factor ('&' Factor)*
     */
    private LogicNode term() {
        LogicNode result = factor();

        while(isType(BooleanTokenType.AND)) {
            advance();
            result = new AndNode(result, factor());
        }

        return result;
    }

    /**
     * Factor = ('!')? Factor
     *        | Atom
     */
    private LogicNode factor() {
        if(isType(BooleanTokenType.NOT)) {
            advance();
            return new NegationNode(factor());
        }

        return atom();
    }

    /**
     * Atom = Action
     *      | Constant
     *      | '(' Formula ')'
     */
    private LogicNode atom() {
        LogicNode result;

        switch(current.type) {
            case ACTION -> result = new ActionNode(current.value);
            case CONSTANT -> result = new ConstantNode(current.value.equals("1") || current.value.equals("true"));
            case LPAREN -> {
                advance();
                result = formula();
                assert(isType(BooleanTokenType.RPAREN));
            }
            default -> throw new IllegalArgumentException("Illegal TokenType " + current.type);
        }

        advance();
        return result;
    }

    /* H E L P E R S */

    private void advance() {
        if(i < booleanTokens.size()) current = booleanTokens.get(i++);
    }

    private boolean isType(BooleanTokenType type) {
        return current.type == type;
    }
}

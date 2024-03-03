package bool.parser;

import bool.lexer.BooleanLexer;
import bool.lexer.token.BooleanToken;
import bool.lexer.token.BooleanTokenType;
import bool.parser.logicnode.*;
import marker.Parser;

public class BooleanParser extends Parser<BooleanTokenType, BooleanToken, LogicNode> {
    public static LogicNode parse(String input) {
        return new BooleanParser().parse(input, BooleanLexer::tokenize);
    }

    @Override
    protected LogicNode start() {
        return formula();
    }

    private LogicNode formula() {
        return doubleImplication();
    }

    private LogicNode doubleImplication() {
        return subRepetitionTTSub_(this::implication, BooleanTokenType.DOUBLE_IMPLICATION, DoubleImplicationNode::new);
    }

    private LogicNode implication() {
        return subRepetitionTTSub_(this::expression, BooleanTokenType.IMPLICATION, ImplicationNode::new);
    }

    private LogicNode expression() {
        return subRepetitionTTSub_(this::term, BooleanTokenType.OR, OrNode::new);
    }

    private LogicNode term() {
        return subRepetitionTTSub_(this::factor, BooleanTokenType.AND, AndNode::new);
    }

    private LogicNode factor() {
        return optionalTT_Sub(BooleanTokenType.NOT, this::atom, NegationNode::new);
    }

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
            default -> throw new IllegalArgumentException("Illegal Token " + current + " at [" + current.line + "|" + current.col + "]");
        }

        advance();
        return result;
    }
}

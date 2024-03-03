package ctl.parser;

import ctl.lexer.CTLLexer;
import ctl.lexer.token.CTLToken;
import ctl.lexer.token.CTLTokenType;
import ctl.parser.ctlnode.*;
import marker.Parser;

public class CTLParser extends Parser<CTLTokenType, CTLToken, CTLNode> {
    public static CTLNode parse(String input) {
        return new CTLParser().parse(input, CTLLexer::tokenize);
    }

    @Override
    protected CTLNode start() {
        return formula();
    }

    private CTLNode formula() {
        return doubleImplication();
    }

    private CTLNode doubleImplication() {
        return subRepetitionTTSub_(this::implication, CTLTokenType.DOUBLE_IMPLICATION, DoubleImplicationNode::new);
    }

    private CTLNode implication() {
        return subRepetitionTTSub_(this::expression, CTLTokenType.IMPLICATION, ImplicationNode::new);
    }

    private CTLNode expression() {
        return subRepetitionTTSub_(this::term, CTLTokenType.OR, OrNode::new);
    }

    private CTLNode term() {
        return subRepetitionTTSub_(this::factor, CTLTokenType.AND, AndNode::new);
    }

    private CTLNode factor() {
        return optionalTT_Sub(CTLTokenType.NOT, this::atom, NegationNode::new);
    }

    private CTLNode atom() {
        CTLNode result;

        return switch(current.type) {
            case EXISTS, FOR_ALL -> temporal();
            case ACTION -> {
                result = new ActionNode(current.value);
                advance();
                yield result;
            }
            case CONSTANT -> {
                result = new ConstantNode(current.value.equals("1") || current.value.equals("true"));
                advance();
                yield result;
            }
            case LPAREN -> {
                advance();
                result = formula();
                check(CTLTokenType.RPAREN);
                yield result;
            }
            default -> throw new IllegalArgumentException("Illegal Token " + current + " at [" + current.line + "|" + current.col + "]");
        };
    }

    private CTLNode temporal() {
        CTLNode result;

        return switch(current.type) {
            case EXISTS -> {
                advance();
                yield switch(current.type) {
                    case IMMEDIATE -> {
                        advance();
                        yield new EXNode(formula());
                    }
                    case FINALLY -> {
                        advance();
                        yield new EFNode(formula());
                    }
                    case GLOBALLY -> {
                        advance();
                        yield new EGNode(formula());
                    }
                    case LPAREN -> {
                        advance();
                        CTLNode left = formula();
                        check(CTLTokenType.UNTIL);
                        result = new EUNode(left, formula());
                        check(CTLTokenType.RPAREN);
                        yield result;
                    }
                    default -> throw new IllegalArgumentException("Illegal Token " + current + " at [" + current.line + "|" + current.col + "]");
                };
            }
            case FOR_ALL -> {
                advance();
                yield switch(current.type) {
                    case IMMEDIATE -> {
                        advance();
                        yield new AXNode(formula());
                    }
                    case FINALLY -> {
                        advance();
                        yield new AFNode(formula());
                    }
                    case GLOBALLY -> {
                        advance();
                        yield new AGNode(formula());
                    }
                    case LPAREN -> {
                        advance();
                        CTLNode left = formula();
                        check(CTLTokenType.UNTIL);
                        result = new AUNode(left, formula());
                        check(CTLTokenType.RPAREN);
                        yield result;
                    }
                    default -> throw new IllegalArgumentException("Illegal Token " + current + " at [" + current.line + "|" + current.col + "]");
                };
            }
            default -> throw new IllegalArgumentException("Illegal Token " + current + " at [" + current.line + "|" + current.col + "]");
        };
    }
}

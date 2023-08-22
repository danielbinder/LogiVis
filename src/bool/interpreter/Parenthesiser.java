package bool.interpreter;

import bool.parser.logicnode.*;

public class Parenthesiser {
    public static String addNecessaryParenthesis(LogicNode formula) {
        return addNecessaryParenthesis(formula, false);
    }

    private static String addNecessaryParenthesis(LogicNode formula, boolean parenthesise) {
        return (parenthesise ? "(" : "") +
                switch(formula) {
                    case ActionNode n -> n.name();
                    case ConstantNode n -> n.bool();
                    case NegationNode n -> "!" + addNecessaryParenthesis(n.child(), childHasLowerPrecedence(n, n.child()));
                    case AndNode n -> addNecessaryParenthesis(n.left(), childHasLowerPrecedence(n, n.left())) + " & " +
                            addNecessaryParenthesis(n.right(), childHasLowerPrecedence(n, n.right()));
                    case OrNode n -> addNecessaryParenthesis(n.left(), childHasLowerPrecedence(n, n.left())) + " | " +
                            addNecessaryParenthesis(n.right(), childHasLowerPrecedence(n, n.right()));
                    case ImplicationNode n -> addNecessaryParenthesis(n.left(), childHasLowerPrecedence(n, n.left())) + " -> " +
                            addNecessaryParenthesis(n.right(), childHasLowerPrecedence(n, n.right()));
                    case DoubleImplicationNode n -> addNecessaryParenthesis(n.left(), childHasLowerPrecedence(n, n.left())) + " & " +
                            addNecessaryParenthesis(n.right(), childHasLowerPrecedence(n, n.right()));
                } +
                (parenthesise ? ")" : "");
    }

    private static boolean childHasLowerPrecedence(LogicNode original, LogicNode child) {
        return switch(original) {
            case ActionNode n -> false;
            case ConstantNode n -> false;
            case NegationNode n -> child instanceof AndNode || child instanceof OrNode ||
                    child instanceof ImplicationNode || child instanceof DoubleImplicationNode;
            case AndNode n -> child instanceof OrNode || child instanceof ImplicationNode || child instanceof DoubleImplicationNode;
            case OrNode n -> child instanceof ImplicationNode || child instanceof DoubleImplicationNode;
            case ImplicationNode n -> child instanceof DoubleImplicationNode;
            case DoubleImplicationNode n -> false;
        };
    }
}

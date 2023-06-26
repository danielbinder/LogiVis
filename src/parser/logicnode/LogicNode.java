package parser.logicnode;

import lexer.Lexer;
import parser.Parser;

public sealed interface LogicNode permits
        ActionNode,
        AndNode,
        ConstantNode,
        DoubleImplicationNode,
        ImplicationNode,
        NegationNode,
        OrNode {
    static LogicNode of(String formula) {
        return new Parser().parse(Lexer.tokenize(formula));
    }
}


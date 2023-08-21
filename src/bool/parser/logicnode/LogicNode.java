package bool.parser.logicnode;

import bool.parser.BooleanParser;

public sealed interface LogicNode permits
        ActionNode,
        AndNode,
        ConstantNode,
        DoubleImplicationNode,
        ImplicationNode,
        NegationNode,
        OrNode {
    static LogicNode of(String formula) {
        return new BooleanParser().parse(formula);
    }
}


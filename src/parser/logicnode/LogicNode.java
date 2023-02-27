package parser.logicnode;

public sealed interface LogicNode permits
        ActionNode,
        AndNode,
        ConstantNode,
        DoubleImplicationNode,
        ImplicationNode,
        NegationNode,
        OrNode {}


package parser.node;

public sealed interface Node permits
        ActionNode,
        AndNode,
        ConstantNode,
        DoubleImplicationNode,
        FinallyNode,
        GloballyNode,
        ImmediateNode,
        ImplicationNode,
        NegationNode,
        OrNode,
        UntilNode {}


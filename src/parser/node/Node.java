package parser.node;

public sealed interface Node permits
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


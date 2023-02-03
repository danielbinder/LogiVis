package parser.logicnode;

public sealed interface LogicNode permits
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


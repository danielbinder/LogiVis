package parser.node;

sealed interface Node {}

/** 0 or 1 */
record ConstantNode(boolean bool) implements Node {}

/** ! child */
record NegationNode(Node child) implements Node {}
/** left & right */
record AndNode(Node left, Node right) implements Node {}
/** left | right */
record OrNode(Node left, Node right) implements Node {}

/** left -> right i.e. !left | right */
record ImplicationNode(Node left, Node right) implements Node {}
/** left <-> right i.e. left -> right & right -> left */
record DoubleImplicationNode(Node left, Node right) implements Node {}

// HML

/** [action]expression i.e. for ALL 'action'-successors of the current state 'expression' holds */
record BracketNode(Node action, Node child) implements Node {}
/** <action>expression i.e. for ONE 'action'-successors of the current state 'expression' holds */
record AngledBracketNode(Node action, Node child) implements Node {}

// CTL/HML

enum PathQuantifier {
    E,      // EXISTS
    A       // FOR ALL
}

/** PathQuantifier X expression i.e. in immediate successors 'expression' holds */
record ImmediateNode(PathQuantifier quantifier, Node child) implements Node {}
/** PathQuantifier F expression i.e. 'expression' holds eventually */
record FinallyNode(PathQuantifier quantifier, Node child) implements Node {}
/** PathQuantifier G expression i.e. 'expression' holds always */
record GloballyNode(PathQuantifier quantifier, Node child) implements Node {}
/** PathQuantifier left U right i.e. left holds until right occurs */
record UntilNode(PathQuantifier quantifier, Node left, Node right) implements Node {}


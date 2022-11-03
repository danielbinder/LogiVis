package parser.node;

/**
 * PathQuantifier left U right i.e., left holds until right occurs
 */
public record UntilNode(PathQuantifier quantifier, Node left, Node right) implements Node {}

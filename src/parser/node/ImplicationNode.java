package parser.node;

/**
 * left -> right i.e., !left | right
 */
public record ImplicationNode(Node left, Node right) implements Node {}

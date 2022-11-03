package parser.node;

/**
 * left | right
 */
public record OrNode(Node left, Node right) implements Node {}

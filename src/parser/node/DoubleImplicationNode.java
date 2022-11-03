package parser.node;

/**
 * left <-> right i.e., left -> right & right -> left
 */
public record DoubleImplicationNode(Node left, Node right) implements Node {}

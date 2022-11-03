package parser.node;

/**
 * PathQuantifier F expression i.e., 'expression' holds eventually
 */
public record FinallyNode(PathQuantifier quantifier, Node child) implements Node {}

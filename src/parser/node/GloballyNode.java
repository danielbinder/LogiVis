package parser.node;

/**
 * PathQuantifier G expression i.e., 'expression' holds always
 */
public record GloballyNode(PathQuantifier quantifier, Node child) implements Node {}

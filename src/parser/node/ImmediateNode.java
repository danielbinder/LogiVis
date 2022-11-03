package parser.node;

/**
 * PathQuantifier X expression i.e., in immediate successor, 'expression' holds
 */
public record ImmediateNode(PathQuantifier quantifier, Node child) implements Node {}

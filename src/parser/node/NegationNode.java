package parser.node;

/**
 * ! child
 */
public record NegationNode(Node child) implements Node {
    @Override
    public String toString() {
        return "(!" + child.toString() + ")";
    }
}

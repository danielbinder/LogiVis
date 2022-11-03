package parser.node;

/**
 * left | right
 */
public record OrNode(Node left, Node right) implements Node {
    @Override
    public String toString() {
        return "(" + left.toString() + " | " + right.toString() + ")";
    }
}

package parser.node;

/**
 * left & right
 */
public record AndNode(Node left, Node right) implements Node {
    @Override
    public String toString() {
        return "(" + left.toString() + " & " + right.toString() + ")";
    }
}

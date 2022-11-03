package parser.node;

/**
 * 0 or 1
 */
public record ConstantNode(boolean bool) implements Node {
    @Override
    public String toString() {
        return bool ? "1" : "0";
    }
}

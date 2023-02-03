package parser.logicnode;

/**
 * 0 or 1
 */
public record ConstantNode(boolean bool) implements LogicNode {
    @Override
    public String toString() {
        return bool ? "1" : "0";
    }
}

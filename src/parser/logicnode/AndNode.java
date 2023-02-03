package parser.logicnode;

/**
 * left & right
 */
public record AndNode(LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " & " + right.toString() + ")";
    }
}

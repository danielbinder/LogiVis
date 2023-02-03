package parser.logicnode;

/**
 * left <-> right i.e., left -> right & right -> left
 */
public record DoubleImplicationNode(LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " <-> " + right.toString() + ")";
    }
}

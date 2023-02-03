package parser.logicnode;

/**
 * left | right
 */
public record OrNode(LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " | " + right.toString() + ")";
    }
}

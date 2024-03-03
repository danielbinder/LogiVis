package bool.parser.logicnode;

/**
 * left -> right i.e., !left | right
 */
public record ImplicationNode(LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " -> " + right.toString() + ")";
    }
}

package bool.parser.logicnode;

/**
 * ! child
 */
public record NegationNode(LogicNode child) implements LogicNode {
    @Override
    public String toString() {
        return "(!" + child.toString() + ")";
    }
}

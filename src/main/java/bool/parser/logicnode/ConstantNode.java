package bool.parser.logicnode;

/**
 * false/0 or true/1
 */
public record ConstantNode(boolean bool) implements LogicNode {
    @Override
    public String toString() {
        return bool ? "true" : "false";
    }
}

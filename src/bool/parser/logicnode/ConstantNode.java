package bool.parser.logicnode;

/**
 * false/0 or true/1
 */
public record ConstantNode(boolean bool) implements LogicNode {
    @Override
    public String toString() {
        return bool ? "true" : "false";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        ConstantNode that = (ConstantNode) o;

        return bool == that.bool;
    }

    @Override
    public int hashCode() {
        return (bool ? 1 : 0);
    }
}

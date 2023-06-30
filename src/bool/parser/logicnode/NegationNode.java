package bool.parser.logicnode;

import java.util.Objects;

/**
 * ! child
 */
public record NegationNode(LogicNode child) implements LogicNode {
    @Override
    public String toString() {
        return "(!" + child.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        NegationNode that = (NegationNode) o;

        return Objects.equals(child, that.child);
    }

    @Override
    public int hashCode() {
        return child != null ? child.hashCode() : 0;
    }
}

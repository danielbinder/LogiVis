package bool.parser.logicnode;

import java.util.Objects;

/** [a-z]+ i.e. represents an action */
public record ActionNode(String name) implements LogicNode {
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        ActionNode that = (ActionNode) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

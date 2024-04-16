package bool.variant.cnf.parser.cnfnode;

import java.util.Objects;

public record Not(Variable child) implements AbstractVariable {
    @Override
    public String toString() {
        return "!" + child;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Not not = (Not) o;

        return Objects.equals(child, not.child);
    }

    @Override
    public int hashCode() {
        return child != null ? child.hashCode() : 0;
    }
}

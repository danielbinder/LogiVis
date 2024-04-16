package bool.variant.cnf.parser.cnfnode;

import java.util.Objects;

public record Variable(String name) implements AbstractVariable {
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

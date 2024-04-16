package bool.variant.cnf.parser.cnfnode;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Clause extends ArrayList<AbstractVariable> {
    @Serial
    private static final long serialVersionUID = 7678731468078357261L;
    public int watchIndex1 = 0;
    public int watchIndex2 = 1;

    public Clause(List<AbstractVariable> variables) {
        super();

        addAll(variables);
    }

    public Clause(AbstractVariable... variables) {
        this(Arrays.asList(variables));
    }

    private Clause(List<AbstractVariable> variables, int watchIndex1, int watchIndex2) {
        this(variables);

        this.watchIndex1 = watchIndex1;
        this.watchIndex2 = watchIndex2;
    }

    public boolean containsVariable(Variable variable) {
        return stream()
                .map(AbstractVariable::getVariable)
                .anyMatch(var -> var.equals(variable));
    }

    public boolean occursInSamePolarity(AbstractVariable variable) {
        return stream()
                .anyMatch(var -> var.getVariable().equals(variable.getVariable()) && var.isPositive() == variable.isPositive());
    }

    public Clause clone() {
        return new Clause(this, watchIndex1, watchIndex2);
    }

    @Override
    public String toString() {
        return stream()
                .map(AbstractVariable::toString)
                .collect(Collectors.joining(" | ", "(", ")"));
    }
}

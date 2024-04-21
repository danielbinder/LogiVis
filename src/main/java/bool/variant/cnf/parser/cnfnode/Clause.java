package bool.variant.cnf.parser.cnfnode;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static bool.variant.cnf.parser.cnfnode.Clause.Status.*;

public class Clause extends ArrayList<AbstractVariable> {
    @Serial
    private static final long serialVersionUID = 7678731468078357261L;
    public int watchIndex1 = 0;
    public int watchIndex2 = 1;

    public Clause(List<AbstractVariable> variables) {
        super();

        addAll(variables);
        watchIndex1 = 0;
        watchIndex2 = 1;
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

    public Status getStatus(Map<Variable, Boolean> assignments) {
        if(isEmpty()) return UNSAT;

        // if this would be SAT or UNSAT, an action would have been taken -> SAT = removed, UNSAT = all unsat
        if(watchIndex1 >= size() || watchIndex2 >= size()) return DECIDABLE;
        AbstractVariable var1 = get(watchIndex1);
        AbstractVariable var2 = get(watchIndex2);

        if(assignments.containsKey(var1.getVariable())) {
            if(assignments.get(var1.getVariable()) == var1.isPositive()) return SAT;

            watchIndex1 = Math.max(watchIndex1, watchIndex2) + 1;
            while(watchIndex1 < size()) {
                if(assignments.containsKey(get(watchIndex1).getVariable())) {
                    if(assignments.get(get(watchIndex1).getVariable()) == get(watchIndex1).isPositive()) return SAT;
                    else watchIndex1++;
                } else return UNKNOWN;
            }

            if(watchIndex2 < size()) return DECIDABLE;
            else return UNSAT;
        }

        if(assignments.containsKey(var2.getVariable())) {
            if(assignments.get(var2.getVariable()) == var2.isPositive()) return SAT;

            watchIndex2 = Math.max(watchIndex1, watchIndex2) + 1;
            while(watchIndex2 < size()) {
                if(assignments.containsKey(get(watchIndex2).getVariable())) {
                    if(assignments.get(get(watchIndex2).getVariable()) == get(watchIndex2).isPositive()) return SAT;
                    else watchIndex2++;
                } else return UNKNOWN;
            }

            if(watchIndex1 < size()) return DECIDABLE;
            else return UNSAT;
        }

        return UNKNOWN;
    }

    /**
     * Only call this if you know status == decidable!
     */
    public AbstractVariable getDecidable(Map<Variable, Boolean> assignments) {
        return watchIndex1 >= size()
                ? get(watchIndex2)
                : get(watchIndex1);
    }

    public void resetWatcherIndices(Map<Variable, Boolean> assignment) {
        watchIndex1 = 0;
        while(watchIndex1 < size() && !assignment.containsKey(get(watchIndex1).getVariable())) watchIndex1++;
        watchIndex2 = watchIndex1 + 1;
        while(watchIndex2 < size() && !assignment.containsKey(get(watchIndex2).getVariable())) watchIndex2++;
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

    public enum Status {
        SAT,
        UNSAT,
        UNKNOWN,
        DECIDABLE       // only one var left undecided
    }
}

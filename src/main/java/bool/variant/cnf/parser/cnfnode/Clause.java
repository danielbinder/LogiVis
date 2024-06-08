package bool.variant.cnf.parser.cnfnode;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

import static bool.variant.cnf.parser.cnfnode.Clause.Status.*;

public class Clause extends ArrayList<AbstractVariable> {
    @Serial
    private static final long serialVersionUID = 7678731468078357261L;
    public int watchIndex1;
    public int watchIndex2;

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

    public boolean containsAllVariables(Collection<Variable> variables) {
        return variables.stream()
                .allMatch(this::containsVariable);
    }

    public boolean occursInSamePolarity(AbstractVariable variable) {
        return stream()
                .anyMatch(var -> var.getVariable().equals(variable.getVariable()) && var.isPositive() == variable.isPositive());
    }

    public Clause resolution(Clause other) {
        Clause result = new Clause();

        // add all unique
        result.addAll(stream()
                              .filter(var -> !other.containsVariable(var.getVariable()))
                              .toList());
        result.addAll(other.stream()
                              .filter(var -> !containsVariable(var.getVariable()))
                              .toList());

        // Do resolution on remaining i.e. only keep the ones whose opposite is not contained in the other
        result.addAll(stream()
                              .filter(var -> other.containsVariable(var.getVariable()))
                              .filter(var -> !other.contains(var.negated()))
                              .toList());

        return result;
    }

    public Status getStatus(Map<Variable, Boolean> assignments) {
        // update both counters until either >= size or SAT
        while(watchIndex1 < size() && assignments.containsKey(get(watchIndex1).getVariable()) &&
                assignments.get(get(watchIndex1).getVariable()) != get(watchIndex1).isPositive()) watchIndex1++;
        while((watchIndex2 < size() && assignments.containsKey(get(watchIndex2).getVariable()) &&
                assignments.get(get(watchIndex2).getVariable()) != get(watchIndex2).isPositive())
                || watchIndex1 == watchIndex2) watchIndex2++;

        if(watchIndex1 >= size() && watchIndex2 >= size()) return UNSAT;

        if(watchIndex1 >= size()) {
            // if containsKey -> SAT, otherwise it would have been incremented earlier
            if(assignments.containsKey(get(watchIndex2).getVariable())) return SAT;
            else return DECIDABLE;
        }
        if(watchIndex2 >= size()) {
            // if containsKey -> SAT, otherwise it would have been incremented earlier
            if(assignments.containsKey(get(watchIndex1).getVariable())) return SAT;
            else return DECIDABLE;
        }
        // if containsKey -> SAT, otherwise it would have been incremented earlier
        if(assignments.containsKey(get(watchIndex1).getVariable()) ||
                assignments.containsKey(get(watchIndex2).getVariable())) return SAT;

        else return UNKNOWN;
    }

    /**
     * Only call this if you know status == decidable!
     */
    public AbstractVariable getDecidable(Map<Variable, Boolean> assignments) {
        return watchIndex1 >= size()
                ? get(watchIndex2)
                : get(watchIndex1);
    }

    public void resetWatcherIndices() {
        watchIndex1 = 0;
        watchIndex2 = 1;
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

package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Preprocessor {
    public static Conjunction sortAscendingClauseSize(Conjunction conjunction) {
        List<Clause> sorted = conjunction.stream()
                .sorted(Comparator.comparingInt(ArrayList::size))
                .toList();
        return new Conjunction(sorted, conjunction.variables, conjunction.assignment);
    }

    public static List<Variable> sortAscendingOccurrence(Conjunction conjunction, List<Variable> variables) {
        return variables.stream()
                .sorted(Comparator.comparingLong(var -> conjunction.stream()
                        .filter(clause -> clause.containsVariable(var))
                        .count()))
                .toList();
    }
}

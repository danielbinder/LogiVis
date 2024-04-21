package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Preprocessor {
    public static Conjunction sortAscending(Conjunction conjunction) {
        List<Clause> sorted = conjunction.stream()
                .sorted(Comparator.comparingInt(ArrayList::size))
                .toList();
        return new Conjunction(sorted, conjunction.variables, conjunction.assignment);
    }
}

package bool.variant.cnf.interpreter;

import bool.interpreter.BooleanAlgebraSolver;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Not;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public sealed interface CNFSolver extends BooleanAlgebraSolver permits
        RecursiveDPLLSolver,
        NonRecursiveLiteralWatchingDPLLSolver,
        CDCLSolver {

    Map<Variable, Boolean> solve(Conjunction conjunction);

    default Map<String, Boolean> solveAndTransform(Conjunction conjunction) {
        return solve(conjunction).entrySet().stream()
                .filter(e -> !(e.getKey().name().equals("true") ||
                                e.getKey().name().equals("false") ||
                                e.getKey().name().startsWith("sub")))
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    default Conjunction BCP(Conjunction conjunction) {
        return switch(this) {
            case RecursiveDPLLSolver ignored -> BooleanConstraintPropagation.recursive(conjunction);
            case NonRecursiveLiteralWatchingDPLLSolver ignored -> BooleanConstraintPropagation.nonRecursiveLiteralWatching(conjunction);
            case CDCLSolver ignored -> BooleanConstraintPropagation.nonRecursiveLiteralWatchingConflictGraphUpdating(conjunction);
        };
    }

    default List<Variable> variablesInBothPolarities(Conjunction conjunction) {
        List<Variable> posPolarity = new ArrayList<>();
        List<Variable> negPolarity = new ArrayList<>();

        conjunction
                .forEach(clause -> clause
                        .forEach(variable -> {
                            switch(variable) {
                                case Variable n -> posPolarity.add(n);
                                case Not n -> negPolarity.add(n.child());
                            }
                        }));

        return posPolarity.stream()
                .filter(negPolarity::contains)
                .filter(var -> !conjunction.assignment.containsKey(var))
                .toList();
    }
}

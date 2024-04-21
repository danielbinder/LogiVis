package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This wraps any CNFSolver to give it extra functionality
 */
public class IncrementalCNFSolver {
    private final CNFSolver solver;
    public boolean unsatisfiable = false;

    public IncrementalCNFSolver(CNFSolver solver) {
        this.solver = solver;
    }

    public List<Map<Variable, Boolean>> solveAll(Conjunction conjunction) {
        Map<Variable, Boolean> model = solver.solve(conjunction.clone());

        if(model.isEmpty()) {
            unsatisfiable = true;
            return List.of(model);
        }

        List<Map<Variable, Boolean>> models = new ArrayList<>();
        while(!model.isEmpty()) {
            models.add(model);

            Clause clause = new Clause();
            model.entrySet().stream()
                    // clause & !(a & b & c & ...) => clause & (!a | !b | !c | ...)
                    .map(e -> e.getValue() ? e.getKey().negated() : e.getKey())
                    .forEach(clause::add);
            conjunction.add(clause);
            model = solver.solve(conjunction.clone());
        }

        return models;
    }

    public List<Map<String, Boolean>> solveAllAndTransform(Conjunction conjunction) {
        return solveAll(conjunction).stream()
                .map(assignment -> assignment.entrySet().stream()
                        .filter(e -> !(e.getKey().name().equals("true") ||
                                e.getKey().name().equals("false") ||
                                e.getKey().name().startsWith("sub")))
                        .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)))
                .toList();
    }
}

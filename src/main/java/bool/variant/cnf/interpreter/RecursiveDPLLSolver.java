package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RecursiveDPLLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        conjunction = BCP(conjunction);
        if(conjunction.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
        if(conjunction.stream().anyMatch(ArrayList::isEmpty)) {
            unsatisfiable = true;
            return Map.of();
        }

        List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
        if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
        Variable bpVar = variablesInBothPolarities.getFirst();

        Map<Variable, Boolean> withBPVar = solve(conjunction.clone().withUnitClause(bpVar));
        if(!withBPVar.isEmpty()) return withBPVar;
        return solve(conjunction.withUnitClause(bpVar.negated()));
    }
}

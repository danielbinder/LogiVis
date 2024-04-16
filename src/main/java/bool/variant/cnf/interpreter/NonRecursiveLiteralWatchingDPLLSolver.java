package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public final class NonRecursiveLiteralWatchingDPLLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        Stack<Conjunction> stack = new Stack<>();

        while(true) {
            conjunction = NRLWBCP(conjunction);
            if(conjunction.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
            if(conjunction.stream().anyMatch(ArrayList::isEmpty)) {
                if(stack.isEmpty()) {
                    unsatisfiable = true;
                    return Map.of();
                }
                conjunction = stack.pop();
            } else {
                List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
                if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
                Variable bpVar = variablesInBothPolarities.getFirst();
                stack.push(conjunction.clone().withUnitClause(bpVar.negated()));
                conjunction = conjunction.withUnitClause(bpVar);
            }
        }
    }
}

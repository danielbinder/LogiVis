package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public final class CDCLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        for(Clause clause : conjunction) {
            if(clause.size() == 1) {
                Clause.Status status = clause.getStatus(conjunction.assignment);
                if(status == Clause.Status.UNSAT) {
                    unsatisfiable = true;
                    return Map.of();
                } else if(status == Clause.Status.DECIDABLE) {
                    conjunction.decisionGraph.deriveUnitDecision(conjunction.assignment, clause.getFirst());
                }
            }
        }

        Stack<Conjunction> stack = new Stack<>();
        while(true) {
            conjunction = BCP(conjunction);
            if(conjunction.decisionGraph.hasConflict()) {
                System.out.println(conjunction.decisionGraph);
                Clause conflictClause = conjunction.decisionGraph.constructConflictClause();
                System.out.println("Conflict clause: " + conflictClause);
                stack.forEach(c -> c.add(conflictClause));
                int backtrackLevel = conjunction.decisionGraph.level;
                System.out.println("Backtrack level: " + backtrackLevel);
                do {
                    if(stack.isEmpty()) {
                        unsatisfiable = true;
                        return Map.of();
                    }
                    conjunction = stack.pop();
                    // A decision in the popped stack has already been made -> backtrackLevel + 1
                } while(conjunction.decisionGraph.level > backtrackLevel + 1);
                System.out.println("Conjunction: " + conjunction);
            } else {
                List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
                if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
                Variable bpVar = variablesInBothPolarities.getFirst();

                Conjunction clone = conjunction.clone();
                clone.decisionGraph.makeDecision(clone.assignment, bpVar.negated());
                conjunction.decisionGraph.makeDecision(conjunction.assignment, bpVar);
                stack.push(clone);

            }
        }
    }
}
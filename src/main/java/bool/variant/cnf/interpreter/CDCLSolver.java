package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;
import util.Logger;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public final class CDCLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        conjunction = Preprocessor.sortAscendingClauseSize(conjunction);

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
                Logger.info(1, conjunction.decisionGraph.toString());
                Clause conflictClause = conjunction.decisionGraph.constructConflictClause(conjunction.assignment);
                Logger.info(1, "Conflict clause: " + conflictClause);
                stack.forEach(c -> c.addFirst(conflictClause));

                int backtrackLevel = conjunction.decisionGraph.level;
                Logger.info(1, "Backtrack level: " + backtrackLevel);
                do {
                    if(stack.isEmpty()) {
                        unsatisfiable = true;
                        return Map.of();
                    }
                    conjunction = stack.pop();
                } while(conjunction.decisionGraph.level > backtrackLevel);
                Logger.info(1, "Backtracked " + conjunction.decisionGraph);
                Logger.info(2, "Backtracked conjunction: " + conjunction, conflictClause.toString());
            } else {
                List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
                if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
                Variable bpVar = variablesInBothPolarities.getFirst();

                Conjunction clone = conjunction.clone();
                stack.push(clone);

                conjunction.decisionGraph.makeDecision(conjunction.assignment, bpVar);
            }
        }
    }
}
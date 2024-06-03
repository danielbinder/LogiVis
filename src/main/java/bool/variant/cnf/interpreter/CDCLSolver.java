package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.List;
import java.util.Map;

public final class CDCLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        for(Clause clause : conjunction) {
            if(clause.getStatus(conjunction.assignment) == Clause.Status.DECIDABLE) {
                conjunction.decisionGraph.deriveUnitDecision(conjunction.assignment, clause.getFirst());
            }
        }

        while(true) {
            conjunction = BCP(conjunction);
            if(conjunction.decisionGraph.hasConflict()) {
                // TODO don't use backtrack level to detect unsat formula
                if(conjunction.decisionGraph.backtrackLevel == 0) {
                    unsatisfiable = true;
                    return Map.of();
                }
                conjunction.backtrack();
            } else {
                List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
                if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
                Variable bpVar = variablesInBothPolarities.getFirst();
                AbstractVariable assignment = conjunction.futureAssignments.stream()
                                .filter(var -> var.getVariable().equals(bpVar))
                                .findFirst()
                                .orElse(bpVar);
                conjunction.futureAssignments.remove(assignment);
                conjunction.futureAssignments.add(assignment.negated());
                conjunction.decisionGraph.makeDecision(conjunction.assignment, assignment);
            }
        }
    }
}
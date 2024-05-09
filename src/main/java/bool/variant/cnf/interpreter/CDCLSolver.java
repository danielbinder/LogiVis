package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.Map;

public final class CDCLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        // TODO csteidl
        throw new IllegalStateException("Not implemented yet!");
//        while(true) {
//            conjunction = BCP(conjunction);
//            if(conjunction.stream().anyMatch(List::isEmpty)) {
//                if(conjunction.decisionGraph.isEmpty()) {
//                    unsatisfiable = true;
//                    return Map.of();
//                }
//                conjunction.backtrack();
//            } else {
//                List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
//                if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
//                Variable bpVar = variablesInBothPolarities.getFirst();
//                AbstractVariable assignment = conjunction.futureAssignments.stream()
//                                .filter(var -> var.getVariable().equals(bpVar))
//                                .findFirst()
//                                .orElse(bpVar);
//                //TODO csteidl futureAssignments is very stupid - is there a better way?
//                conjunction.futureAssignments.remove(assignment);
//                conjunction.futureAssignments.add(assignment.negated());
//                conjunction.assignment.put(assignment.getVariable(), assignment.isPositive());
//                conjunction.decisionGraph.decide(assignment);
//            }
//        }
    }
}
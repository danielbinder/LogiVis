package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.List;
import java.util.Map;

public final class CDCLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        while(true) {
            conjunction = BCP(conjunction);
            if(conjunction.decisionGraph.conflict) {
                if(conjunction.decisionGraph.isEmpty()) return Map.of();
                AbstractVariable firstUIP = conjunction.decisionGraph.getFirstAndLastUIP().left;
                conjunction.backtrackUIP(firstUIP);
            } else {
                List<Variable> variablesInBothPolarities = variablesInBothPolarities(conjunction);
                if(variablesInBothPolarities.isEmpty()) return conjunction.withRemainingClausesAssignedTrue().assignment;
                Variable bpVar = variablesInBothPolarities.getFirst();
                conjunction.decisionGraph.decide(bpVar);
            }
        }
    }
}
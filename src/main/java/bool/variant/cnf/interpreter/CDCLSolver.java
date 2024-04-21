package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.util.Map;

public final class CDCLSolver implements CNFSolver {
    public boolean unsatisfiable = false;

    @Override
    public Map<Variable, Boolean> solve(Conjunction conjunction) {
        return null;
    }
}
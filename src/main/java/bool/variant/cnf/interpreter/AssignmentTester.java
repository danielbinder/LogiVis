package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.Conjunction;
import bool.variant.cnf.parser.cnfnode.Variable;
import util.Logger;

import java.util.Map;

public class AssignmentTester {
    public static boolean isValidAssignment(Conjunction conjunction, Map<Variable, Boolean> assignment) {
        if(assignment.size() < conjunction.variables.size()) {
            Logger.warning("Not all variables assigned to a value! Expected " + conjunction.variables.size() +
                                   " but got " + assignment.size());
            return false;
        }

        return conjunction.stream()
                .allMatch(clause -> clause.stream()
                        .anyMatch(var -> assignment.get(var.getVariable()) == var.isPositive()));
    }
}

package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BooleanConstraintPropagation {
    public static Conjunction recursive(Conjunction conjunction) {
        List<Clause> unitClauses = conjunction.stream()
                .filter(clause -> clause.size() == 1)
                .toList();
        if(unitClauses.isEmpty()) return conjunction;

        AbstractVariable unitClauseVariable = unitClauses.getFirst().getFirst();
        conjunction.assignment.put(unitClauseVariable.getVariable(), unitClauseVariable.isPositive());

        conjunction.stream()
                .filter(clause -> clause.containsVariable(unitClauseVariable.getVariable()))
                .map(clause -> clause.occursInSamePolarity(unitClauseVariable)
                        ? (Runnable) () -> conjunction.removeSafe(clause)
                        : (Runnable) () -> clause.remove(unitClauseVariable.negated()))
                // Collect to not execute on collection that is being iterated
                .toList()
                .forEach(Runnable::run);

        return recursive(conjunction);
    }

    public static Conjunction nonRecursive(Conjunction conjunction) {
        List<Clause> unitClauses = conjunction.stream()
                .filter(clause -> clause.size() == 1)
                .toList();

        while(!unitClauses.isEmpty()) {
            AbstractVariable unitClauseVariable = unitClauses.getFirst().getFirst();
            conjunction.assignment.put(unitClauseVariable.getVariable(), unitClauseVariable.isPositive());

            unitClauses = conjunction.stream()
                    .filter(clause -> clause.size() == 1)
                    .toList();
        }

        return conjunction;
    }

    public static Conjunction nonRecursiveLiteralWatching(Conjunction conjunction) {
        boolean actionTaken;
        do {
            actionTaken = false;

            List<Clause> toRemove = new ArrayList<>();
            for(Clause clause : conjunction) {
                // status is derived from clause
                Clause.Status status = clause.getStatus(conjunction.assignment);
                if(status == Clause.Status.UNSAT) return Conjunction.UNSAT_CONJUNCTION;

                if(status == Clause.Status.SAT) toRemove.add(clause);
                else if(status == Clause.Status.DECIDABLE) {
                    AbstractVariable decidable = clause.getDecidable(conjunction.assignment);
                    if(conjunction.assignment.containsKey(decidable.getVariable())) {
                        if(conjunction.assignment.get(decidable.getVariable()) != decidable.isPositive())
                            return Conjunction.UNSAT_CONJUNCTION;
                        else toRemove.add(clause);
                    } else {
                        conjunction.assignment.put(decidable.getVariable(), decidable.isPositive());
                        toRemove.add(clause);
                        actionTaken = true;
                    }
                }
            }

            toRemove.forEach(conjunction::removeSafe);

        } while(actionTaken);

        return conjunction;
    }

    public static Conjunction nonRecursiveLiteralWatchingConflictGraphUpdating(Conjunction conjunction) {
        if(conjunction.decisionGraph.isEmpty()) return conjunction;

        boolean actionTaken;
        do {
            List<Clause> decidables = conjunction.stream()
                    .filter(clause -> clause.getStatus(conjunction.assignment) == Clause.Status.DECIDABLE)
                    .toList();

            actionTaken = !decidables.isEmpty();

            boolean sat = conjunction.decisionGraph.deriveDecisions(conjunction.assignment, decidables);
            if(!sat) return Conjunction.UNSAT_CONJUNCTION;

            conjunction.removedClauses.putAll(decidables.stream()
                                                      .collect(Collectors.groupingBy(decidable -> decidable.getDecidable(conjunction.assignment).getVariable())));
            decidables.forEach(conjunction::removeSafe);
        } while(actionTaken);

        return conjunction;
    }
}

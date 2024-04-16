package bool.variant.cnf.interpreter;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Conjunction;

import java.util.List;

import static bool.variant.cnf.parser.cnfnode.Conjunction.UNSAT_CONJUNCTION;

public class BooleanConstraintPropagation {
    public static Conjunction of(Conjunction conjunction) {
        List<Clause> unitClauses = conjunction.stream()
                .filter(clause -> clause.size() == 1)
                .toList();
        if(unitClauses.isEmpty()) return conjunction;

        AbstractVariable unitClauseVariable = unitClauses.getFirst().getFirst();
        conjunction.assignment.put(unitClauseVariable.getVariable(), unitClauseVariable.isPositive());

        conjunction.stream()
                .filter(clause -> clause.containsVariable(unitClauseVariable.getVariable()))
                .map(clause -> clause.occursInSamePolarity(unitClauseVariable)
                        ? (Runnable) () -> removeSafe(conjunction, clause)
                        : (Runnable) () -> clause.remove(unitClauseVariable.negated()))
                // Collect to not execute on collection that is being iterated
                .toList()
                .forEach(Runnable::run);

        return of(conjunction);
    }

    public static Conjunction nonRecursiveLiteralWatchingOf(Conjunction conjunction) {
        List<Clause> unitClauses = conjunction.stream()
                .filter(clause -> clause.size() == 1)
                .toList();

        while(!unitClauses.isEmpty()) {
            AbstractVariable unitClauseVariable = unitClauses.getFirst().getFirst();
            conjunction.assignment.put(unitClauseVariable.getVariable(), unitClauseVariable.isPositive());
            conjunction = triggerWatcherUpdate(conjunction, unitClauseVariable);

            unitClauses = conjunction.stream()
                    .filter(clause -> clause.size() == 1)
                    .toList();
        }

        return conjunction;
    }

    private static Conjunction triggerWatcherUpdate(Conjunction conjunction, AbstractVariable variable) {
        conjunction.variableReferences.get(variable.getVariable()).stream()
                .map(clause -> clause.occursInSamePolarity(variable)
                        ? (Runnable) () -> removeSafe(conjunction, clause)
                        : (Runnable) () -> removeSafe(clause, variable.negated()))
                // Collect to not execute on collection that is being iterated
                .toList()
                .forEach(Runnable::run);

        conjunction.variableReferences.get(variable.getVariable()).clear();

        boolean unsat = conjunction.variableReferences.get(variable.getVariable()).stream()
                .anyMatch(clause ->
                    clause.watchIndex1 < clause.size() && clause.get(clause.watchIndex1).getVariable().equals(variable)
                        ? !updateWatchIndex1(conjunction, clause)
                        : clause.watchIndex2 < clause.size() &&
                            clause.get(clause.watchIndex2).getVariable().equals(variable) &&
                            !updateWatchIndex2(conjunction, clause));

        return unsat ? UNSAT_CONJUNCTION : conjunction;
    }

    /**
     * @return true iff clause not unsat
     */
    private static boolean updateWatchIndex1(Conjunction conjunction, Clause clause) {
        clause.watchIndex1 = Math.max(clause.watchIndex1, clause.watchIndex2) + 1;

        while(clause.watchIndex1 < clause.size()) {
            if(conjunction.assignment.containsKey(clause.get(clause.watchIndex1).getVariable())) clause.watchIndex1++;
            else return true;
        }

        // here: watchIndex1 == clause.size() -> watch2 is possibly the only remaining watcher
        if(clause.watchIndex2 < clause.size()) {
            AbstractVariable var2 = clause.get(clause.watchIndex2);
            if(conjunction.assignment.containsKey(var2.getVariable()))
                // if watch2 is assigned and is not assigned to value that makes this clause true -> unsat
                return conjunction.assignment.get(var2.getVariable()) == var2.isPositive();

            conjunction.withUnitClause(var2);
        } else return false;

        return true;
    }

    /**
     * @return true iff clause not unsat
     */
    private static boolean updateWatchIndex2(Conjunction conjunction, Clause clause) {
        clause.watchIndex2 = Math.max(clause.watchIndex1, clause.watchIndex2) + 1;

        while(clause.watchIndex2 < clause.size()) {
            if(conjunction.assignment.containsKey(clause.get(clause.watchIndex2).getVariable())) clause.watchIndex2++;
            else return true;
        }

        // here: watchIndex2 == clause.size() -> watch1 is possibly the only remaining watcher
        if(clause.watchIndex1 < clause.size()) {
            AbstractVariable var1 = clause.get(clause.watchIndex1);
            if(conjunction.assignment.containsKey(var1.getVariable()))
                // if watch1 is assigned and is not assigned to value that makes this clause true -> unsat
                return conjunction.assignment.get(var1.getVariable()) == var1.isPositive();

            conjunction.withUnitClause(var1);
        } else return false;

        return true;
    }

    private static void removeSafe(Conjunction conjunction, Clause toRemove) {
        if(conjunction.contains(toRemove)) for(AbstractVariable var : toRemove) {
            conjunction.variableReferences.get(var.getVariable()).remove(toRemove);

            if(conjunction.variableReferences.get(var.getVariable()).isEmpty())
                conjunction.assignment.put(var.getVariable(), var.isPositive());
        }

        conjunction.remove(toRemove);
    }

    private static void removeSafe(Clause clause, AbstractVariable toRemove) {
        int varIndex = clause.indexOf(toRemove);

        if(clause.watchIndex1 >= varIndex) clause.watchIndex1--;
        if(clause.watchIndex2 >= varIndex) clause.watchIndex2--;

        clause.remove(toRemove);
    }
}

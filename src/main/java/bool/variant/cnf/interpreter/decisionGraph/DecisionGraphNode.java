package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Variable;
import util.Pair;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

/** List<Pair<Decision, List<Reasons>>> */
public class DecisionGraphNode extends ArrayList<List<Pair<AbstractVariable, List<AbstractVariable>>>> {
    @Serial
    private static final long serialVersionUID = -3326289578466580792L;

    public DecisionGraphNode(AbstractVariable decision) {
        add(List.of(Pair.of(decision, List.of())));
    }

    public boolean contains(AbstractVariable var) {
        return stream()
                .flatMap(Collection::stream)
                .map(pair -> pair.left)
                .anyMatch(v -> v.equals(var));
    }

    public List<Variable> getDecisionVariables() {
        return stream()
                .flatMap(Collection::stream)
                .map(pair -> pair.left.getVariable())
                .toList();
    }

    public List<Clause> getConflictClauses() {
        return stream()
                .flatMap(Collection::stream)
                .map(decisions -> decisions.right.stream()
                        .map(AbstractVariable::negated)
                        .toList())
                .map(Clause::new)
                .toList();
    }

    public boolean deriveDecision(Map<Variable, Boolean> assignments, List<Clause> toDecide) {
        Set<AbstractVariable> decisions = toDecide.stream()
                .map(clause -> clause.getDecidable(assignments))
                .collect(Collectors.toSet());

        // if any conflicting assignments => conflict
        if(decisions.stream().map(AbstractVariable::getVariable).collect(Collectors.toSet()).size() < decisions.size())
            return false;

        // add all at current level
        add(toDecide.stream()
                .map(clause -> {
                    AbstractVariable decision = clause.getDecidable(assignments);
                    return Pair.of(decision, clause.stream()
                            .filter(var -> !var.equals(decision))
                            .toList());
                })
                .toList());

        return true;
    }

    @Override
    public String toString() {
        return stream()
                .map(Object::toString)
                .collect(Collectors.joining(" -> "));
    }
}

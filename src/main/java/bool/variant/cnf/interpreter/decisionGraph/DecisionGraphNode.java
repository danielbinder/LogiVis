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

    public List<AbstractVariable> getReasons(AbstractVariable var) {
        return stream()
                .flatMap(Collection::stream)
                .filter(pair -> pair.left.equals(var))
                .findAny()
                .orElseThrow(NoSuchElementException::new)
                .right;
    }

    public List<Clause> getConflictClausesStartingFrom(AbstractVariable startPoint) {
        int startIndex = stream()
                .filter(decisions -> decisions.stream().anyMatch(pair -> pair.left.equals(startPoint)))
                .map(this::indexOf)
                .findAny()
                .orElseThrow(NoSuchElementException::new);

        return stream()
                .filter(decisions -> indexOf(decisions) < startIndex)
                .flatMap(Collection::stream)
                .map(decisions -> decisions.right.stream()
                        .map(AbstractVariable::negated)
                        .toList())
                .map(Clause::new)
                .toList();
    }

    public boolean deriveDecisions(Map<Variable, Boolean> assignments, List<Clause> toDecide) {
        Set<AbstractVariable> decisions = toDecide.stream()
                .map(clause -> clause.getDecidable(assignments))
                .collect(Collectors.toSet());

        // if any conflicting assignments => conflict
        if(decisions.stream().map(AbstractVariable::getVariable).distinct().count() < decisions.size())
            return false;

        // add all at current level
        add(toDecide.stream()
                .map(clause -> {
                    AbstractVariable decision = clause.getDecidable(assignments);
                    assignments.put(decision.getVariable(), decision.isPositive());
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

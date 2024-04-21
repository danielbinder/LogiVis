package bool.variant.cnf.interpreter.confictGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Variable;
import util.Pair;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Pair<Decision, List<Reasons>> */
public class DecisionGraphNode extends ArrayList<Pair<AbstractVariable, List<AbstractVariable>>> {
    @Serial
    private static final long serialVersionUID = -3326289578466580792L;

    public DecisionGraphNode(AbstractVariable decision) {
        add(Pair.of(decision, List.of()));
    }

    public boolean contains(AbstractVariable var) {
        return stream()
                .map(pair -> pair.left)
                .anyMatch(v -> v.equals(var));
    }

    public List<Variable> getDecisionVariables() {
        return stream()
                .map(pair -> pair.left.getVariable())
                .toList();
    }

    public List<Clause> getConflictClauses() {
        return stream()
                .map(decisions -> decisions.right.stream()
                        .map(AbstractVariable::negated)
                        .toList())
                .map(Clause::new)
                .toList();
    }

    public void deriveDecision(Map<Variable, Boolean> assignments, Clause toDecide) {
        AbstractVariable decision = toDecide.getDecidable(assignments);
        add(Pair.of(decision,
                    toDecide.stream()
                            .filter(var -> var != decision)
                            .toList()));
    }

    @Override
    public String toString() {
        return stream()
                .map(Object::toString)
                .collect(Collectors.joining(" -> "));
    }
}

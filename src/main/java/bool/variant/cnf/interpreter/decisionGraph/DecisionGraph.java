package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Variable;
import util.Pair;

import java.io.Serial;
import java.util.*;

public class DecisionGraph extends ArrayList<DecisionGraphNode> {
    @Serial
    private static final long serialVersionUID = -71482563341139446L;

    public boolean contains(AbstractVariable var) {
        return stream().anyMatch(node -> node.contains(var));
    }

    public void decide(AbstractVariable decision) {
        add(new DecisionGraphNode(decision));
    }

    public boolean deriveDecisions(Map<Variable, Boolean> assignments, List<Clause> toDecide) {
        return getLast().deriveDecisions(assignments, toDecide);
    }

    public AbstractVariable findDecision(AbstractVariable variable) {
        return findDecisionNode(variable)
                .getFirst()
                .getFirst()
                .left;
    }

    public List<AbstractVariable> getReasonsFor(AbstractVariable variable) {
        return findDecisionNode(variable)
                .getReasons(variable);
    }

    public Clause constructConflictClause() {
        List<Clause> conflictClauses = getLast()
                .getConflictClausesStartingFrom(getFirstAndLastUIP().left);

        // construct conflict clause
        Clause resultingConflictClause = conflictClauses.removeFirst();
        for(Clause conflictClause : conflictClauses)
            resultingConflictClause = resultingConflictClause.resolution(conflictClause);

        // minimize
        int oldConflictClauseSize;
        do {
            oldConflictClauseSize = resultingConflictClause.size();
            Clause finalResultingConflictClause = resultingConflictClause;
            resultingConflictClause = new Clause(resultingConflictClause.stream()
                               .filter(var -> !finalResultingConflictClause.containsAll(getReasonsFor(var)))
                               .toList());
        } while(resultingConflictClause.size() < oldConflictClauseSize);

        return resultingConflictClause;
    }

    public DecisionGraphNode findDecisionNode(AbstractVariable variable) {
        return stream()
                .filter(node -> node.contains(variable))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    public DecisionGraphNode getBackJumpingNode(List<AbstractVariable> conflictParticipants) {
        return conflictParticipants.stream()
                // filter out highest decision level
                .filter(var -> !getLast().contains(var))
                .map(this::findDecisionNode)
                .max((node1, node2) -> Integer.compare(indexOf(node1), indexOf(node2)))
                // if none found, the highest decision level is the only possible one
                .orElse(getLast());
    }

    public Pair<AbstractVariable, AbstractVariable> getFirstAndLastUIP() {
        AbstractVariable lastUIP = getLast().getFirst().getFirst().left;
        AbstractVariable firstUIP = lastUIP;

        int size = 1;
        for(List<Pair<AbstractVariable, List<AbstractVariable>>> decisions : getLast()) {
            if(size == 1) firstUIP = decisions.getFirst().left;

            Set<AbstractVariable> toTrack = new HashSet<>();
            if(decisions.size() > 1) {
                size += decisions.size() - 1;
            }

            decisions.stream()
                    .map(decision -> decision.left)
                    .forEach(toTrack::add);

            size -= (int) decisions.stream()
                    .map(decision -> decision.right)
                    .filter(reasons -> reasons.stream().filter(toTrack::contains).count() > 1)
                    .mapToLong(Collection::size)
                    .sum();
        }

        return Pair.of(firstUIP, lastUIP);
    }
}

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
    public boolean conflict = false;

    public boolean contains(AbstractVariable var) {
        return stream().anyMatch(node -> node.contains(var));
    }

    public void decide(AbstractVariable decision) {
        add(new DecisionGraphNode(decision));
    }

    private void deriveDecision(Map<Variable, Boolean> assignments, List<Clause> toDecide) {
        getLast().deriveDecision(assignments, toDecide);
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

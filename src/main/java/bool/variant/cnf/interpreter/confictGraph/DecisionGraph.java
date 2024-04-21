package bool.variant.cnf.interpreter.confictGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Map;

public class DecisionGraph extends ArrayList<DecisionGraphNode> {
    @Serial
    private static final long serialVersionUID = -71482563341139446L;

    public boolean contains(AbstractVariable var) {
        return stream().anyMatch(node -> node.contains(var));
    }

    public void decide(AbstractVariable decision) {
        add(new DecisionGraphNode(decision));
    }

    private void deriveDecision(Map<Variable, Boolean> assignments, Clause toDecide) {
        getLast().deriveDecision(assignments, toDecide);
    }
}

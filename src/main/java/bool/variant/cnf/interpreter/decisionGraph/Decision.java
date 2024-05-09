package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;

class Decision extends DecisionGraphNode {
    final AbstractVariable decision;

    Decision(int level, AbstractVariable decision) {
        super(level);
        this.decision = decision;
    }
}

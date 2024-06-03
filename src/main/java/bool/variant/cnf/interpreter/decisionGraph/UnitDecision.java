package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;

public class UnitDecision extends Decision {

    UnitDecision(AbstractVariable decision) {
        super(0, decision);
    }
}

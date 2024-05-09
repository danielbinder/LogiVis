package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;

import java.util.Set;

final class ForcedDecision extends Decision implements HasAncestors {
    final Set<Decision> ancestors;

    ForcedDecision(int level, AbstractVariable decision, Set<Decision> ancestors) {
        super(level, decision);

        this.ancestors = ancestors;
    }
}

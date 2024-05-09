package bool.variant.cnf.interpreter.decisionGraph;

import java.util.Set;

final class Conflict extends DecisionGraphNode implements HasAncestors {
    Set<Decision> ancestors;

    Conflict(int level, Set<Decision> ancestors) {
        super(level);

        this.ancestors = ancestors;
    }
}

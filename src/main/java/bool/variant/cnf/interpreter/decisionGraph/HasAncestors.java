package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

sealed interface HasAncestors permits ForcedDecision, Conflict {
    default int getLevel() {
        return switch(this) {
            case ForcedDecision fd -> fd.level;
            case Conflict c -> c.level;
        };
    }

    default Set<Decision> getDirectAncestors() {
        return switch(this) {
            case ForcedDecision fd -> fd.ancestors;
            case Conflict c -> c.ancestors;
        };
    }

    default DecisionGraphNode toDecisionGraphNode() {
        return switch(this) {
            case ForcedDecision fd -> fd;
            case Conflict c -> c;
        };
    }

    default List<Decision> getAllAncestorsOnSameLevel() {
        List<Decision> ancestors = new ArrayList<>();

        getAllAncestorsOnSameLevel(this.toDecisionGraphNode(), ancestors);
        if(this instanceof ForcedDecision fd) ancestors.remove(fd);

        return ancestors;
    }

    default Clause getConflictClause() {
        Clause result = new Clause(getDirectAncestors().stream()
                           .map(decision -> decision.decision.negated())
                           .toList());

        if(this instanceof ForcedDecision fd) result.add(fd.decision);

        return result;
    }

    default Set<AbstractVariable> getReasons() {
        return getDirectAncestors().stream()
                .map(decision -> decision.decision)
                .collect(Collectors.toSet());
    }

    private static void getAllAncestorsOnSameLevel(DecisionGraphNode current, List<Decision> ancestors) {
        switch(current) {
            case ForcedDecision fd -> {
                if(!ancestors.contains(fd)) {
                    ancestors.add(fd);

                    fd.getDirectAncestors()
                            .stream()
                            .filter(ancestor -> ancestor.level == fd.level)
                            .forEach(ancestor -> getAllAncestorsOnSameLevel(ancestor, ancestors));
                }
            }
            case Decision d -> {
                if(!ancestors.contains(d)) ancestors.add(d);
            }
            default -> {}
        }
    }
}

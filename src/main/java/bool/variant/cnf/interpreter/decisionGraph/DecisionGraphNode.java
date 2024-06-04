package bool.variant.cnf.interpreter.decisionGraph;

abstract class DecisionGraphNode implements Comparable<DecisionGraphNode> {
    final int level;

    DecisionGraphNode(int level) {
        this.level = level;
    }

    @Override
    public int compareTo(DecisionGraphNode o) {
        if(this instanceof Conflict) return 1;
        if(o instanceof Conflict) return -1;

        if(this instanceof ForcedDecision fd1 && o instanceof ForcedDecision fd2)
            return fd1.getAllAncestorsOnSameLevel().size() - fd2.getAllAncestorsOnSameLevel().size();

        if(this instanceof HasAncestors) return 1;
        if(o instanceof HasAncestors) return -1;

        return 0;
    }
}

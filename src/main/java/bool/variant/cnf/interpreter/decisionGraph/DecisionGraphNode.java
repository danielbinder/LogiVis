package bool.variant.cnf.interpreter.decisionGraph;

abstract class DecisionGraphNode {
    final int level;

    DecisionGraphNode(int level) {
        this.level = level;
    }
}

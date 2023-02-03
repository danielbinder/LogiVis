package parser.logicnode;

/**
 * PathQuantifier left U right i.e., left holds until right occurs
 */
public record UntilNode(PathQuantifier quantifier, LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + quantifier + " " + left.toString() + " U " + right.toString() + ")";
    }
}

package parser.logicnode;

/**
 * PathQuantifier F expression i.e., 'expression' holds eventually
 */
public record FinallyNode(PathQuantifier quantifier, LogicNode child) implements LogicNode {
    @Override
    public String toString() {
        return "(" + quantifier + "F " + child.toString() + ")";
    }
}

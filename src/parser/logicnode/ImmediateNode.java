package parser.logicnode;

/**
 * PathQuantifier X expression i.e., in immediate successor, 'expression' holds
 */
public record ImmediateNode(PathQuantifier quantifier, LogicNode child) implements LogicNode {
    @Override
    public String toString() {
        return "(" + quantifier + "X " + child.toString() + ")";
    }
}

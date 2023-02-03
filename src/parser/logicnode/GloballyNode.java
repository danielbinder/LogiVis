package parser.logicnode;

/**
 * PathQuantifier G expression i.e., 'expression' holds always
 */
public record GloballyNode(PathQuantifier quantifier, LogicNode child) implements LogicNode {
    @Override
    public String toString() {
        return "(" + quantifier + "G " + child.toString() + ")";
    }
}

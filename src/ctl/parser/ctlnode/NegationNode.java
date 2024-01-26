package ctl.parser.ctlnode;

/**
 * ! child
 */
public record NegationNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(!" + child.toString() + ")";
    }
}

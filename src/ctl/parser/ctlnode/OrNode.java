package ctl.parser.ctlnode;

/**
 * left | right
 */
public record OrNode(CTLNode left, CTLNode right) implements CTLNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " | " + right.toString() + ")";
    }
}

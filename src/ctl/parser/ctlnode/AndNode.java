package ctl.parser.ctlnode;

/**
 * left & right
 */
public record AndNode(CTLNode left, CTLNode right) implements CTLNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " & " + right.toString() + ")";
    }
}

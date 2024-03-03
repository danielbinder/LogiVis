package ctl.parser.ctlnode;

/**
 * left -> right i.e., !left | right
 */
public record ImplicationNode(CTLNode left, CTLNode right) implements CTLNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " -> " + right.toString() + ")";
    }
}

package ctl.parser.ctlnode;

public record AUNode(CTLNode left, CTLNode right) implements CTLNode {
    @Override
    public String toString() {
        return "(A (" + left + " U " + right + "))";
    }
}

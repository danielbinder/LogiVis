package ctl.parser.ctlnode;

public record EUNode(CTLNode left, CTLNode right) implements CTLNode {
    @Override
    public String toString() {
        return "(E (" + left + " U " + right + "))";
    }
}

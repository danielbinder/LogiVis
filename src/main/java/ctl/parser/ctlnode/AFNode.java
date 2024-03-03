package ctl.parser.ctlnode;

public record AFNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(AF " + child + ")";
    }
}

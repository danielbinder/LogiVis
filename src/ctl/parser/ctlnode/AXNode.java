package ctl.parser.ctlnode;

public record AXNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(AX " + child + ")";
    }
}

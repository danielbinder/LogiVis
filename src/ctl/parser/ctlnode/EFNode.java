package ctl.parser.ctlnode;

public record EFNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(EF " + child + ")";
    }
}

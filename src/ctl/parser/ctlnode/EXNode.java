package ctl.parser.ctlnode;

public record EXNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(EX " + child + ")";
    }
}

package ctl.parser.ctlnode;

public record EGNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(EG " + child + ")";
    }
}

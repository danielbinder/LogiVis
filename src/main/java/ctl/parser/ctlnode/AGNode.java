package ctl.parser.ctlnode;

public record AGNode(CTLNode child) implements CTLNode {
    @Override
    public String toString() {
        return "(AG " + child + ")";
    }
}

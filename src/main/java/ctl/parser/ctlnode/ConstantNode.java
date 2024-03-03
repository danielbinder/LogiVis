package ctl.parser.ctlnode;

/**
 * false/0 or true/1
 */
public record ConstantNode(boolean bool) implements CTLNode {
    @Override
    public String toString() {
        return bool ? "true" : "false";
    }
}

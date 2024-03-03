package ctl.parser.ctlnode;

/** [a-z]+ i.e. represents an action */
public record ActionNode(String name) implements CTLNode {
    @Override
    public String toString() {
        return name;
    }
}

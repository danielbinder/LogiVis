package parser.node;

/** [a-z]+ i.e. represents an action */
public record ActionNode(String name) implements Node {
    @Override
    public String toString() {
        return name;
    }
}

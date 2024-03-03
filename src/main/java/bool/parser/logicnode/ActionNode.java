package bool.parser.logicnode;

/** [a-z]+ i.e. represents an action */
public record ActionNode(String name) implements LogicNode {
    @Override
    public String toString() {
        return name;
    }
}

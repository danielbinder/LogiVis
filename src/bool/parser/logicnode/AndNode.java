package bool.parser.logicnode;

import java.util.Objects;

/**
 * left & right
 */
public record AndNode(LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " & " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        AndNode andNode = (AndNode) o;

        if(!Objects.equals(left, andNode.left)) return false;
        return Objects.equals(right, andNode.right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}

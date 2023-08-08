package bool.parser.logicnode;

import java.util.Objects;

/**
 * left <-> right i.e., left -> right & right -> left
 */
public record DoubleImplicationNode(LogicNode left, LogicNode right) implements LogicNode {
    @Override
    public String toString() {
        return "(" + left.toString() + " <-> " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        DoubleImplicationNode that = (DoubleImplicationNode) o;

        if(!Objects.equals(left, that.left)) return false;
        return Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
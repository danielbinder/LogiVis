package util;

import java.util.Objects;

public class Pair<A, B> {
    public final A left;
    public final B right;

    private Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public static <A, B> Pair<A, B> of(A left, B right) {
        return new Pair<>(left, right);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if(!Objects.equals(left, pair.left)) return false;
        return Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + ", " + right.toString() + ")";
    }
}

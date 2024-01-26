package marker;

import java.util.Objects;

public abstract class Token<T extends TokenType> {
    public final T type;
    public final String value;
    public final int line;
    public final int col;

    protected Token(T type, String value, int line, int col) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.col = col;
    }

    protected Token(T type, int line, int col) {
        this(type, "", line, col);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Token<?> token = (Token<?>) o;

        if(line != token.line) return false;
        if(col != token.col) return false;
        if(!Objects.equals(type, token.type)) return false;
        return Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + line;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return type.toString() +
                "[" + line + "|" + col + "]" +
                (value.isEmpty() ? "" : ":" + value);
    }
}

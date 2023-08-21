package bool.token;

import marker.Token;

import java.util.Objects;

public class BooleanToken implements Token {
    public final BooleanTokenType type;
    public final String value;
    public final int line;
    public final int col;

    public BooleanToken(BooleanTokenType type, String value, int line, int col) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.col = col;
    }

    public BooleanToken(BooleanTokenType type, int line, int col) {
        this(type, "", line, col);
    }

    public static BooleanToken fromString(String s, int line, int col) {
        BooleanTokenType type = BooleanTokenType.fromString(s);

        return new BooleanToken(type, type == BooleanTokenType.ACTION || type == BooleanTokenType.CONSTANT ? s : "", line, col);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        BooleanToken booleanToken = (BooleanToken) o;

        if(type != booleanToken.type) return false;
        return Objects.equals(value, booleanToken.value);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type.toString() + (type == BooleanTokenType.ACTION ? ":" + value : "");
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getCol() {
        return col;
    }
}

package bool.token;

import marker.Token;

import java.util.Objects;

public class BooleanToken implements Token {
    public final BooleanTokenType type;
    public final String value;

    public BooleanToken(BooleanTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public BooleanToken(BooleanTokenType type) {
        this(type, "");
    }

    public static BooleanToken fromString(String s) {
        BooleanTokenType type = BooleanTokenType.fromString(s);

        return new BooleanToken(type, type == BooleanTokenType.ACTION || type == BooleanTokenType.CONSTANT ? s : "");
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
}

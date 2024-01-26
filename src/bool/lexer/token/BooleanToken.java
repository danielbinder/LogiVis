package bool.lexer.token;

import marker.Token;

public class BooleanToken extends Token<BooleanTokenType> {
    public BooleanToken(BooleanTokenType type, String value, int line, int col) {
        super(type, value, line, col);
    }

    public BooleanToken(BooleanTokenType type, int line, int col) {
        super(type, "", line, col);
    }

    public static BooleanToken fromString(String s, int line, int col) {
        BooleanTokenType type = BooleanTokenType.fromString(s);

        return new BooleanToken(type, type == BooleanTokenType.ACTION || type == BooleanTokenType.CONSTANT ? s : "", line, col);
    }

    @Override
    public String toString() {
        return type.toString() +
                "[" + line + "|" + col + "]" +
                (type == BooleanTokenType.ACTION ? ":" + value : "");
    }
}

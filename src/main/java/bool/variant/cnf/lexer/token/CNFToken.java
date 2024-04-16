package bool.variant.cnf.lexer.token;

import marker.Token;

public class CNFToken extends Token<CNFTokenType> {
    public CNFToken(CNFTokenType type, String value, int line, int col) {
        super(type, value, line, col);
    }

    public CNFToken(CNFTokenType type, int line, int col) {
        super(type, "", line, col);
    }

    public static CNFToken fromString(String s, int line, int col) {
        CNFTokenType type = CNFTokenType.fromString(s);

        return new CNFToken(type,
                            type == CNFTokenType.NUMBER
                                    ? s
                                    : "", line, col);
    }

    @Override
    public String toString() {
        return type.toString() +
                "[" + line + "|" + col + "]" +
                (type == CNFTokenType.END_OF_LINE ? ":" + value : "");
    }
}

package ctl.lexer.token;

import marker.Token;

public class CTLToken extends Token<CTLTokenType> {
    protected CTLToken(CTLTokenType type, String value, int line, int col) {
        super(type, value, line, col);
    }

    protected CTLToken(CTLTokenType type, int line, int col) {
        super(type, line, col);
    }

    public static CTLToken fromString(String s, int line, int col) {
        CTLTokenType type = CTLTokenType.fromString(s);

        return new CTLToken(type, type == CTLTokenType.ACTION || type == CTLTokenType.CONSTANT ? s : "", line, col);
    }

    @Override
    public String toString() {
        return type.toString() +
                "[" + line + "|" + col + "]" +
                (type == CTLTokenType.ACTION ? ":" + value : "");
    }
}

package lexer.token;

import java.util.Objects;

public class Token {
    public final TokenType type;
    public final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type) {
        this(type, "");
    }

    public static Token fromString(String s) {
        TokenType type = TokenType.fromString(s);

        return new Token(type, type == TokenType.ACTION || type == TokenType.CONSTANT ? s : "");
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if(type != token.type) return false;
        return Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type.toString() + (type == TokenType.ACTION ? ":" + value : "");
    }
}

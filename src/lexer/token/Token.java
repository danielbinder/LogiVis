package lexer.token;

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
        return new Token(TokenType.fromString(s), s);
    }
}

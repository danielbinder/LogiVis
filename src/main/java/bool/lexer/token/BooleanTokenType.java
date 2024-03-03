package bool.lexer.token;

import marker.TokenType;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum BooleanTokenType implements TokenType {
    CONSTANT(CONSTANT_PATTERN),
    ACTION(NAME_PATTERN),

    NOT(NOT_PATTERN),
    AND(AND_PATTERN),
    OR(OR_PATTERN),

    IMPLICATION(IMPLICATION_PATTERN),
    DOUBLE_IMPLICATION(DOUBLE_IMPLICATION_PATTERN),

    LPAREN(LPAREN_PATTERN),
    RPAREN(RPAREN_PATTERN),

    EOF(EOF_PATTERN)
    ;

    public final Pattern pattern;

    BooleanTokenType(Pattern pattern) {
        this.pattern = pattern;
    }

    public static BooleanTokenType fromString(String s) {
        return Arrays.stream(values())
                .filter(v -> s.matches(v.pattern.pattern()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static boolean isValidCharacter(char c) {
        return String.valueOf(c).matches("[a-z0-9&|!\\-><()]");
    }
}

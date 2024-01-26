package model.lexer.token;

import marker.TokenType;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum ModelTokenType implements TokenType {
    PART_TYPE(Pattern.compile("[SITF]")),

    NAME(PROPERTY_PATTERN),
    STRING(STRING_PATTERN),

    EQUALS(EQUALS_PATTERN),
    COMMA(COMMA_PATTERN),

    ENCODING_START(START_PATTERN),
    ENCODING_END(END_PATTERN),

    UNDERSCORE(UNDERSCORE_PATTERN),
    STAR(STAR_PATTERN),
    UNIDIRECTIONAL_TRANSITION(IMPLICATION_PATTERN),
    BIDIRECTIONAL_TRANSITION(MINUS_PATTERN),

    LPAREN(LPAREN_PATTERN),
    RPAREN(RPAREN_PATTERN),
    LBRACKET(LBRACKET_PATTERN),
    RBRACKET(RBRACKET_PATTERN),
    LBRACE(LBRACE_PATTERN),
    RBRACE(RBRACE_PATTERN),

    EOF(EOF_PATTERN)
    ;

    public final Pattern pattern;

    ModelTokenType(Pattern pattern) {
        this.pattern = pattern;
    }

    public static ModelTokenType fromString(String s) {
        return Arrays.stream(values())
                .filter(v -> s.matches(v.pattern.pattern()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static boolean isValidCharacter(char c) {
        return String.valueOf(c).matches("[a-z0-9SITF=,><_*!'\\-()\\[\\]{}]");
    }
}

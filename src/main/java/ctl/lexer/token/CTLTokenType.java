package ctl.lexer.token;

import marker.TokenType;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum CTLTokenType implements TokenType {
    CONSTANT(CONSTANT_PATTERN),
    ACTION(NAME_PATTERN),

    NOT(NOT_PATTERN),
    AND(AND_PATTERN),
    OR(OR_PATTERN),

    IMPLICATION(IMPLICATION_PATTERN),
    DOUBLE_IMPLICATION(DOUBLE_IMPLICATION_PATTERN),

    LPAREN(LPAREN_PATTERN),
    RPAREN(RPAREN_PATTERN),

    EXISTS(Pattern.compile("E")),
    FOR_ALL(Pattern.compile("A")),

    IMMEDIATE(Pattern.compile("X")),
    FINALLY(Pattern.compile("F")),
    GLOBALLY(Pattern.compile("G")),
    UNTIL(Pattern.compile("U")),

    EOF(EOF_PATTERN)
    ;

    public final Pattern pattern;

    CTLTokenType(Pattern pattern) {
        this.pattern = pattern;
    }

    public static CTLTokenType fromString(String s) {
        return Arrays.stream(values())
                .filter(v -> s.matches(v.pattern.pattern()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static boolean isValidCharacter(char c) {
        return String.valueOf(c).matches("[a-z0-9&|!\\-><()EAXFGU]");
    }
}

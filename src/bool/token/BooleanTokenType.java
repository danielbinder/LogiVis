package bool.token;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum BooleanTokenType {
    CONSTANT(Pattern.compile("[01]|true|false")),
    ACTION(Pattern.compile("[a-z]+([a-z]*[0-9]*)*")),

    NOT(Pattern.compile("!")),
    AND(Pattern.compile("&")),
    OR(Pattern.compile("\\|")),

    IMPLICATION(Pattern.compile("->")),
    DOUBLE_IMPLICATION(Pattern.compile("<->")),

    LPAREN(Pattern.compile("\\(")),
    RPAREN(Pattern.compile("\\)")),

    EXISTS(Pattern.compile("E")),
    FOR_ALL(Pattern.compile("A")),

    IMMEDIATE(Pattern.compile("X")),
    FINALLY(Pattern.compile("F")),
    GLOBALLY(Pattern.compile("G")),
    UNTIL(Pattern.compile("U"))
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
}

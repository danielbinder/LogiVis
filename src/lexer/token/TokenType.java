package lexer.token;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum TokenType {
    ACTION(Pattern.compile("[a-z]+")),

    NOT(Pattern.compile("!")),
    AND(Pattern.compile("&")),
    OR(Pattern.compile("\\|")),

    IMPLICATION(Pattern.compile("->")),
    DOUBLE_IMPLICATION(Pattern.compile("<->")),

    LBRACKET(Pattern.compile("\\[")),
    RBRACKET(Pattern.compile("]")),
    LANGLED(Pattern.compile("<")),
    RANGLED(Pattern.compile(">")),

    EXISTS(Pattern.compile("E")),
    FOR_ALL(Pattern.compile("A")),

    IMMEDIATE(Pattern.compile("X")),
    FINALLY(Pattern.compile("F")),
    GLOBALLY(Pattern.compile("G")),
    UNTIL(Pattern.compile("U"))
    ;

    public final Pattern pattern;

    TokenType(Pattern pattern) {
        this.pattern = pattern;
    }

    public static TokenType fromString(String s) {
        return Arrays.stream(values())
                .filter(v -> s.matches(v.pattern.pattern()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}

package model.token;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum ModelTokenType {
    PART_TYPE(Pattern.compile("[SITF]")),

    NAME(Pattern.compile("!?[a-z][a-z0-9]*")),
    STRING(Pattern.compile("'.*'")),

    EQUALS(Pattern.compile("=")),
    COMMA(Pattern.compile(",")),

    ENCODING_START(Pattern.compile(">")),
    ENCODING_END(Pattern.compile("<")),

    UNDERSCORE(Pattern.compile("_")),
    STAR(Pattern.compile("\\*")),
    UNIDIRECTIONAL_TRANSITION(Pattern.compile("->")),
    BIDIRECTIONAL_TRANSITION(Pattern.compile("-")),

    LPAREN(Pattern.compile("\\(")),
    RPAREN(Pattern.compile("\\)")),
    LBRACKET(Pattern.compile("\\[")),
    RBRACKET(Pattern.compile("]")),
    LBRACE(Pattern.compile("\\{")),
    RBRACE(Pattern.compile("}")),

    EOF(Pattern.compile("EOF"))
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
}

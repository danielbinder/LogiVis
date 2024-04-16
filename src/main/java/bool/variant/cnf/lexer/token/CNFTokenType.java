package bool.variant.cnf.lexer.token;

import marker.TokenType;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public enum CNFTokenType implements TokenType {
    P(Pattern.compile("p")),
    CNF(Pattern.compile("cnf")),
    END_OF_LINE(Pattern.compile("0")),

    NUMBER(NUMBER_PATTERN),

    EOF(EOF_PATTERN)
    ;

    public final Pattern pattern;

    CNFTokenType(Pattern pattern) {
        this.pattern = pattern;
    }

    public static CNFTokenType fromString(String s) {
        return Arrays.stream(values())
                .filter(v -> s.matches(v.pattern.pattern()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static boolean isValidCharacter(char c) {
        return String.valueOf(c).matches("[\\-pcnf0-9]");
    }
}

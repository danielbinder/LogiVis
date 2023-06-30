package lexer;

import bool.token.BooleanToken;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexer {
    public static List<BooleanToken> tokenizeBooleanFormula(final String input) {
        final List<BooleanToken> booleanTokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            if(Character.isWhitespace(input.charAt(i))) continue;

            current.append(input.charAt(i));

            if(Character.isAlphabetic(input.charAt(i)) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) ||
                    Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                booleanTokens.add(BooleanToken.fromString(current.toString()));       // this is where the magic happens
                current = new StringBuilder();
            } catch(NoSuchElementException ignored) {}
        }

        return booleanTokens;
    }
}

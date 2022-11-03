package lexer;

import lexer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexer {
    public static List<Token> tokenize(final String input) {
        final List<Token> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            if(Character.isWhitespace(input.charAt(i))) continue;

            current.append(input.charAt(i));

            try {
                tokens.add(Token.fromString(current.toString()));
                current = new StringBuilder();
            } catch(NoSuchElementException ignored) {}
        }

        return tokens;
    }
}

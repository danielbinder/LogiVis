package lexer;

import bool.token.BooleanToken;
import model.token.ModelToken;

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

    public static List<ModelToken> tokenizeModel(final String input) {
        boolean openString = false;
        int line = 1;
        int col = 0;
        final List<ModelToken> modelTokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            col++;
            if(Character.isWhitespace(input.charAt(i))) {
                if(input.charAt(i) == '\n') {
                    line++;
                    col = 0;
                }

                if(!openString) continue;
            }

            if(input.charAt(i) == '#') {
                while(i + 1 < input.length() && input.charAt(i + 1) != '\n') i++;
                continue;
            }
            if(input.charAt(i) == '\'') openString = !openString;
            current.append(input.charAt(i));

            if(Character.isAlphabetic(input.charAt(i)) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) ||
                            Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                if(input.charAt(i) == '-') continue;
                modelTokens.add(ModelToken.fromString(current.toString(), line, col));       // this is where the magic happens
                current = new StringBuilder();
            } catch(NoSuchElementException ignored) {}
        }

        return modelTokens;
    }
}

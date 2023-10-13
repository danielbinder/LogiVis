package lexer;

import bool.token.BooleanToken;
import bool.token.BooleanTokenType;
import model.token.ModelToken;
import model.token.ModelTokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexer {
    public static List<BooleanToken> tokenizeBooleanFormula(final String input) {
        int line = 1;
        int col = 0;
        final List<BooleanToken> booleanTokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            col++;
            if(Character.isWhitespace(input.charAt(i))) {
                if(input.charAt(i) == '\n') {
                    line++;
                    col = 0;
                }

                continue;
            }

            if(!BooleanTokenType.isValidCharacter(input.charAt(i))) error(input, line, col);
            current.append(input.charAt(i));

            if((Character.isAlphabetic(input.charAt(i)) || Character.isDigit(input.charAt(i))) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) || Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                booleanTokens.add(BooleanToken.fromString(current.toString(), line, col));       // this is where the magic happens
                current = new StringBuilder();
            } catch(NoSuchElementException ignored) {}
        }

        booleanTokens.add(BooleanToken.fromString("EOF", line, col));

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

            // Read over comments
            if(input.charAt(i) == '#') {
                while(i + 1 < input.length() && input.charAt(i + 1) != '\n') i++;
                continue;
            }

            // Take anything inside String
            if(input.charAt(i) == '\'') openString = !openString;
            if(!openString && !ModelTokenType.isValidCharacter(input.charAt(i))) error(input, line, col);
            current.append(input.charAt(i));

            if((Character.isAlphabetic(input.charAt(i)) || Character.isDigit(input.charAt(i))) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) || Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                if(input.charAt(i) == '-' && i + 1 < input.length() && input.charAt(i + 1) == '>') continue;
                modelTokens.add(ModelToken.fromString(current.toString(), line, col));       // this is where the magic happens
                current = new StringBuilder();
            } catch(NoSuchElementException ignored) {}
        }

        modelTokens.add(ModelToken.fromString("EOF", line, col));

        return modelTokens;
    }

    private static void error(String input, int line, int col) {
        String lineStr = input.split("\n")[line - 1];
        if(lineStr.length() > 40) {
            lineStr = lineStr.substring(Math.max(0, col - 40), Math.min(lineStr.length(), col + 40));
            col = col - 40 < 0 ? col : 40;
        }

        System.out.println(lineStr);
        System.out.println(" ".repeat(col - 1) + "^");
        throw new IllegalArgumentException("Illegal character or sequence starting with '" +
                                                   lineStr.charAt(col - 1) +
                                                   "' at [" + line + "|" + col + "]");
    }
}

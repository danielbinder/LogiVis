package marker;

import util.Error;
import util.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public abstract class Lexer {
    protected static <T extends Token<? extends TokenType>> List<T> tokenize(String input,
                                                                             Predicate<Character> characterValidator,
                                                                             TriFunction<String, Integer, Integer, T> tokenCreator) {
        int line = 1;
        int col = 0;
        boolean openString = false;
        final List<T> tokens = new ArrayList<>();
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
            if(!openString && !characterValidator.test(input.charAt(i))) error(input, line, col);
            current.append(input.charAt(i));

            if(((Character.isAlphabetic(input.charAt(i)) && Character.isLowerCase(input.charAt(i))) || Character.isDigit(input.charAt(i))) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) || Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                try {
                    tokenCreator.apply(current.toString() + input.charAt(i + 1), line, col);
                    continue;
                } catch(Exception ignored) {}
                tokens.add(tokenCreator.apply(current.toString(), line, col));       // this is where the magic happens
                current = new StringBuilder();
            } catch(NoSuchElementException ignored) {}
        }

        tokens.add(tokenCreator.apply("EOF", line, col));

        return tokens;
    }

    private static void error(String input, int line, int col) {
        String lineStr = input.split("\n")[line - 1];
        Error.printPosition(lineStr, col);
        throw new IllegalArgumentException("Illegal character or sequence starting with '" +
                                                   lineStr.charAt(col - 1) +
                                                   "' at [" + line + "|" + col + "]");
    }
}

package lexer;

import bool.token.BooleanToken;
import marker.Token;
import model.token.ModelToken;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexer {
    public static List<BooleanToken> tokenizeBooleanFormula(final String input) {
        int line = 1;
        int col = 0;
        int charsRead = 0;
        int charsReadTemp = 0;
        final List<BooleanToken> booleanTokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            col++;
            charsReadTemp++;
            if(Character.isWhitespace(input.charAt(i))) {
                if(input.charAt(i) == '\n') {
                    line++;
                    col = 0;
                }

                continue;
            }

            current.append(input.charAt(i));

            if((Character.isAlphabetic(input.charAt(i)) || Character.isDigit(input.charAt(i))) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) || Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                booleanTokens.add(BooleanToken.fromString(current.toString(), line, col));       // this is where the magic happens
                current = new StringBuilder();
                charsRead += charsReadTemp;
                charsReadTemp = 0;
            } catch(NoSuchElementException ignored) {}
        }

        charsRead += charsReadTemp;
        checkError(charsRead, input, booleanTokens);

        booleanTokens.add(BooleanToken.fromString("EOF", line, col));

        return booleanTokens;
    }

    public static List<ModelToken> tokenizeModel(final String input) {
        boolean openString = false;
        int line = 1;
        int col = 0;
        int charsRead = 0;
        int charsReadTemp = 0;
        final List<ModelToken> modelTokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            col++;
            charsReadTemp++;
            if(Character.isWhitespace(input.charAt(i))) {
                if(input.charAt(i) == '\n') {
                    line++;
                    col = 0;
                }

                if(!openString) continue;
            }

            // Read over comments
            if(input.charAt(i) == '#') {
                while(i + 1 < input.length() && input.charAt(i + 1) != '\n') {
                    charsReadTemp++;
                    i++;
                }
                continue;
            }

            // Take anything inside String
            if(input.charAt(i) == '\'') openString = !openString;
            current.append(input.charAt(i));

            if((Character.isAlphabetic(input.charAt(i)) || Character.isDigit(input.charAt(i))) &&
                    i + 1 < input.length() &&
                    (Character.isAlphabetic(input.charAt(i + 1)) || Character.isDigit(input.charAt(i + 1)))) continue;

            try {
                if(input.charAt(i) == '-' && i + 1 < input.length() && input.charAt(i + 1) == '>') continue;
                modelTokens.add(ModelToken.fromString(current.toString(), line, col));       // this is where the magic happens
                current = new StringBuilder();
                charsRead += charsReadTemp;
                charsReadTemp = 0;
            } catch(NoSuchElementException ignored) {}
        }

        charsRead += charsReadTemp;
        checkError(charsRead, input, modelTokens);

        modelTokens.add(ModelToken.fromString("EOF", line, col));

        return modelTokens;
    }

    private static void checkError(int charsRead, String input, List<? extends Token> tokens) {
        if(charsRead == 0) {
            String lineStr = input.split("\n")[0];
            System.out.println(lineStr.substring(0, Math.min(lineStr.length(), 80)));
            System.out.println("^");
            throw new IllegalArgumentException("Illegal character or sequence starting with '" +
                                                       lineStr.charAt(0) + "' at [0|0]");
        }

        if(charsRead < input.length() - 1) {
            Token last = tokens.get(tokens.size() - 1);
            String lineStr = input.split("\n")[last.getLine() - 1];
            int col = last.getCol();
            if(lineStr.length() > 40) {
                lineStr = lineStr.substring(Math.max(0, col - 40), Math.min(lineStr.length(), col + 40));
                col = col - 40 < 0 ? col : 40;
            }

            //  && col < lineStr.length() should NOT be necessary here - if it is, there's an error beforehand!
            if(tokens.get(tokens.size() - 1) == last) {
                System.out.println(lineStr);
                int whiteSpaces = 0;
                while(Character.isWhitespace(lineStr.charAt(col + whiteSpaces))) whiteSpaces++;
                System.out.println(" ".repeat(col + whiteSpaces) + "^");
                throw new IllegalArgumentException("Illegal character or sequence starting with '" +
                                                           lineStr.charAt(col + whiteSpaces) +
                                                           "' at [" + last.getLine() + "|" + (col + whiteSpaces) + "]");
            }
        }
    }
}

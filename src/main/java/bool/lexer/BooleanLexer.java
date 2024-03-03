package bool.lexer;

import bool.lexer.token.BooleanToken;
import bool.lexer.token.BooleanTokenType;
import marker.Lexer;

import java.util.List;

public class BooleanLexer extends Lexer {
    public static List<BooleanToken> tokenize(String input) {
        return Lexer.tokenize(input, BooleanTokenType::isValidCharacter, BooleanToken::fromString);
    }
}

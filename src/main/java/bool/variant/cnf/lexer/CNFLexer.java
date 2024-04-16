package bool.variant.cnf.lexer;

import bool.variant.cnf.lexer.token.CNFToken;
import bool.variant.cnf.lexer.token.CNFTokenType;
import marker.Lexer;

import java.util.List;

public class CNFLexer extends Lexer {
    public static List<CNFToken> tokenize(String input) {
        return Lexer.tokenize(input, CNFTokenType::isValidCharacter, CNFToken::fromString);
    }
}

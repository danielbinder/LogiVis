package model.lexer;

import marker.Lexer;
import model.lexer.token.ModelToken;
import model.lexer.token.ModelTokenType;

import java.util.List;

public class ModelLexer extends Lexer {
    public static List<ModelToken> tokenize(String input) {
        return Lexer.tokenize(input, ModelTokenType::isValidCharacter, ModelToken::fromString);
    }
}

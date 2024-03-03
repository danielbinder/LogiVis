package ctl.lexer;

import ctl.lexer.token.CTLToken;
import ctl.lexer.token.CTLTokenType;
import marker.Lexer;

import java.util.List;

public class CTLLexer extends Lexer {
    public static List<CTLToken> tokenize(String input) {
        return Lexer.tokenize(input, CTLTokenType::isValidCharacter, CTLToken::fromString);
    }
}

import lexer.Lexer;
import lexer.token.Token;
import lexer.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {
    @Test
    public void genericTest() {
        assertEquals(List.of(new Token(TokenType.ACTION, "a"),
                             new Token(TokenType.IMPLICATION),
                             new Token(TokenType.ACTION, "b")),
                     Lexer.tokenize("a -> b"));
    }
}

import lexer.Lexer;
import bool.token.BooleanToken;
import bool.token.BooleanTokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {
    @Test
    public void testGeneric() {
        assertEquals(List.of(new BooleanToken(BooleanTokenType.ACTION, "a"),
                             new BooleanToken(BooleanTokenType.IMPLICATION),
                             new BooleanToken(BooleanTokenType.ACTION, "b")),
                     Lexer.tokenizeBooleanFormula("a -> b"));
    }
}

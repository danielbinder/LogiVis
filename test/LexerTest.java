import lexer.Lexer;
import bool.token.BooleanToken;
import bool.token.BooleanTokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {
    @Test
    public void testGeneric() {
        assertEquals(List.of(new BooleanToken(BooleanTokenType.ACTION, "a", 0, 0),
                             new BooleanToken(BooleanTokenType.IMPLICATION, 0, 2),
                             new BooleanToken(BooleanTokenType.ACTION, "b", 0, 4)),
                     Lexer.tokenizeBooleanFormula("a -> b"));
    }
}

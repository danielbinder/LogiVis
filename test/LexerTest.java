import bool.token.BooleanToken;
import bool.token.BooleanTokenType;
import lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {
    @Test
    public void testGeneric() {
        assertEquals(List.of(new BooleanToken(BooleanTokenType.ACTION, "a", 0, 0),
                             new BooleanToken(BooleanTokenType.IMPLICATION, 0, 2),
                             new BooleanToken(BooleanTokenType.ACTION, "b", 0, 4),
                             new BooleanToken(BooleanTokenType.EOF, 0, 4)),
                     Lexer.tokenizeBooleanFormula("a -> b"));
    }

    @Test
    public void testIllegalCharStart() {
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenizeModel(":a"));
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenizeBooleanFormula(":b"));
    }

    @Test
    public void testIllegalCharMiddle() {
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenizeModel("a : b"));
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenizeBooleanFormula("a : b"));
    }

    @Test
    public void testIllegalCharEnd() {
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenizeModel("a :"));
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenizeBooleanFormula("a :"));
    }
}

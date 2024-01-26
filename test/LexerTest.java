import bool.lexer.BooleanLexer;
import bool.lexer.token.BooleanToken;
import bool.lexer.token.BooleanTokenType;
import model.lexer.ModelLexer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {
    @Test
    public void testGeneric() {
        assertEquals(List.of(new BooleanToken(BooleanTokenType.ACTION, "a", 1, 1),
                             new BooleanToken(BooleanTokenType.IMPLICATION, 1, 4),
                             new BooleanToken(BooleanTokenType.ACTION, "b", 1, 6),
                             new BooleanToken(BooleanTokenType.EOF, 1, 6)),
                     BooleanLexer.tokenize("a -> b"));
    }

    @Test
    public void testIllegalCharStart() {
        assertThrows(IllegalArgumentException.class, () -> ModelLexer.tokenize(":a"));
        assertThrows(IllegalArgumentException.class, () -> BooleanLexer.tokenize(":b"));
    }

    @Test
    public void testIllegalCharMiddle() {
        assertThrows(IllegalArgumentException.class, () -> ModelLexer.tokenize("a : b"));
        assertThrows(IllegalArgumentException.class, () -> BooleanLexer.tokenize("a : b"));
    }

    @Test
    public void testIllegalCharEnd() {
        assertThrows(IllegalArgumentException.class, () -> ModelLexer.tokenize("a :"));
        assertThrows(IllegalArgumentException.class, () -> BooleanLexer.tokenize("a :"));
    }
}

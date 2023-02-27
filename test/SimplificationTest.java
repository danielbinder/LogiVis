import interpreter.Simplification;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimplificationTest {
    @Test
    public void testSimple() {
        test("a & (a | b)", "a");
    }

    private void test(String input, String expected) {
        assertEquals(expected, runInput(input));
    }

    private String runInput(String input) {
        return Simplification.of(new Parser().parse(Lexer.tokenize(input))).toString();
    }
}

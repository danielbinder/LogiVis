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

    @Test
    public void testNoSimplification() {
        test("a & b", "(a & b)");
    }

    @Test
    public void testDoubleNegation() {
        test("!!a", "a");
    }

    @Test
    public void testNegationElimination() {
        test("!(!a & !b)", "(a | b)");
    }

    @Test
    public void testIdentity() {
        test("a & true", "a");
        test("a | false", "a");
    }

    @Test
    public void testDomination() {
        test("a & false", "false");
        test("a | true", "true");
    }

    @Test
    public void testIdempotence() {
        test("a & a", "a");
        test("a | a", "a");
    }

    @Test
    public void testDoubleNegationSimplification() {
        test("!!(!a)", "(!a)");
    }

    @Test
    public void testDistributiveSimplification() {
        test("(a | b) | (a | c)", "(a | (b | c))");
    }

    @Test
    public void testPrecedenceSimplification() {
        test("a | b & a | c", "(a | c)");
    }

    @Test
    public void testInverseElementSimplification() {
        test("a & !a", "false");
        test("!a & a", "false");
        test("a | !a", "true");
        test("!a | a", "true");
    }

    @Test
    public void testAbsorption() {
        test("a & (a | b)", "a");
        test("a | (a & b)", "a");
    }

    private void test(String input, String expected) {
        assertEquals(expected, runInput(input));
    }

    private String runInput(String input) {
        return Simplification.of(new Parser().parse(Lexer.tokenize(input))).toString();
    }
}

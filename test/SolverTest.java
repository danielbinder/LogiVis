import interpreter.BruteForceSolver;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SolverTest {
    @Test
    public void testPrecedenceFormula() {
        String formula = "a | !b & c <-> !(a | b) & c";
        var expected = Map.of("a", "false", "b", "false", "c", "false");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testImplicationPrecedence() {
        String formula = "(p -> q) | (q -> p)";
        var expected = Map.of("p", "false", "q", "false");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testAndOrFormula() {
        String formula = "(a & b) | c";
        var expected = Map.of("a", "false", "b", "false", "c", "true");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testTautology() {
        String formula = "a & a";
        var expected = Map.of("a", "true");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testUnsatisfiable() {
        String formula = "a & !a";
        assertNull(getAssignment(formula));
    }

    @Test
    public void testConstant() {
        String formula = "a & !b | true";
        var expected = Map.of("a", "false", "b", "false");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testConjunction() {
        String formula = "a & !b & c & d";
        var expected = Map.of("a", "true", "b", "false", "c", "true", "d", "true");
        assertEquals(expected, getAssignment(formula));
    }

    private Map<String, String> getAssignment(String formula) {
        var varAssignment = BruteForceSolver.solve(new Parser().parse(Lexer.tokenize(formula)));
        System.out.println(varAssignment);
        return varAssignment;
    }
}

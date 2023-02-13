import interpreter.BruteForceSolver;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class SolverTest {
    @Test
    public void testPrecedenceFormula() {
        String formula = "a | !b & c <-> !(a | b) & c";
        Map<String, String> expected = Map.of("a", "false", "b", "false", "c", "false");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testImplicationPrecedence() {
        String formula = "(p -> q) | (q -> p)";
        Map<String, String> expected = Map.of("p", "false", "q", "false");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testAndOrFormula() {
        String formula = "(a & b) | c";
        Map<String, String> expected = Map.of("a", "false", "b", "false", "c", "true");
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testTautology() {
        String formula = "a & a";
        Map<String, String> expected = Map.of("a", "true");
        assertEquals(expected, getAssignment(formula));
    }

    private Map<String, String> getAssignment(String formula) {
        Map<String, String> varAssignment = BruteForceSolver.solve(new Parser().parse(Lexer.tokenize(formula)));
        System.out.println(varAssignment);
        return varAssignment;
    }
}

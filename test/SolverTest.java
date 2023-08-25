import bool.interpreter.BruteForceSolver;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Assumption: Lexer and Parser are correct!
 */
public class SolverTest {
    @Test
    public void testPrecedenceFormula() {
        String formula = "a | !b & c <-> !(a | b) & c";
        var expected = new HashMap<>(Map.of("a", false, "b", false, "c", false));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testImplicationPrecedence() {
        String formula = "(p -> q) | (q -> p)";
        var expected = new HashMap<>(Map.of("p", false, "q", false));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testAndOrFormula() {
        String formula = "(a & b) | c";
        var expected = new HashMap<>(Map.of("a", false, "b", false, "c", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testTautology() {
        String formula = "a & a";
        var expected = new HashMap<>(Map.of("a", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testUnsatisfiable() {
        String formula = "a & !a";
        assertEquals(Map.of(), getAssignment(formula));
    }

    @Test
    public void testConstant() {
        String formula = "a & !b | true";
        var expected = new HashMap<>(Map.of("a", false, "b", false));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testConjunction() {
        String formula = "a & !b & c & d";
        var expected = new HashMap<>(Map.of("a", true, "b", false, "c", true, "d", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testAllSolutions() {
        String formula = "a -> b & !c";
        var expected = List.of(
                new HashMap<>(Map.of("a", false, "b", false, "c", false)),
                new HashMap<>(Map.of("a", false, "b", false, "c", true)),
                new HashMap<>(Map.of("a", false, "b", true, "c", false)),
                new HashMap<>(Map.of("a", false, "b", true, "c", true)),
                new HashMap<>(Map.of("a", true, "b", true, "c", false)));
        assertEquals(expected, getAllAssignments(formula));
    }

    @Test
    public void testValid() {
        String formula = "a | !a";
        assertEquals(List.of(Map.of("a", false), Map.of("a", true)), new BruteForceSolver(formula).solveAll());
    }

    @Test
    public void testLongVariableNames() {
        String formula = "apples | !bananas & carrots <-> !(apples | bananas) & carrots";
        var expected = new HashMap<>(Map.of("apples", false, "bananas", false, "carrots", false));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testSingleVariable() {
        String formula = "a";
        var expected = new HashMap<>(Map.of("a", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testRepeatedVariables() {
        String formula = "a | !a & a";
        var expected = new HashMap<>(Map.of("a", true));
        assertEquals(expected, getAssignment(formula));
    }


    @Test
    public void testParentheses() {
        String formula = "a & (b | c)";
        var expected = new HashMap<>(Map.of("a", true, "b", false, "c", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testMixedFormula() {
        String formula = "(a | b) & (c | d)";
        var expected = new HashMap<>(Map.of("a", false, "b", true, "c", false, "d", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testMixedOperators() {
        String formula = "(a & b) <-> (c | d)";
        var expected = new HashMap<>(Map.of("a", false, "b", false, "c", false, "d", false));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testOnlyNegations() {
        String formula = "!(a & b)";
        var expected = new HashMap<>(Map.of("a", false, "b", false));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testOnlyConjunctions() {
        String formula = "a & b & c";
        var expected = new HashMap<>(Map.of("a", true, "b", true, "c", true));
        assertEquals(expected, getAssignment(formula));
    }


    @Test
    public void testOnlyDisjunctions() {
        String formula = "a | b | c";
        var expected = new HashMap<>(Map.of("a", false, "b", false, "c", true));
        assertEquals(expected, getAssignment(formula));
    }

    @Test
    public void testVariablesWithNumbers() {
        String formula = "(a0 & b0) | c0";
        var expected = new HashMap<>(Map.of("a0", true, "b0", true, "c0", false));
        assertEquals(expected, getAssignment(formula));
    }

    private Map<String, Boolean> getAssignment(String formula) {
        var assignment = new BruteForceSolver(formula).solve();
        System.out.println(assignment);
        return assignment;
    }

    private List<Map<String, Boolean>> getAllAssignments(String formula) {
        var allAssignments = new BruteForceSolver(formula).solveAll();
        System.out.println(allAssignments);
        return allAssignments;
    }
}

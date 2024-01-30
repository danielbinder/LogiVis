import ctl.interpreter.CTLSolver;
import ctl.parser.ctlnode.CTLNode;
import model.parser.Model;
import model.variant.kripke.KripkeStructure;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CTLTest {
    private static final String SIMPLE_KRIPKE = "s1_ [!p !q] -> s2_ [p q], s1 -> s3 [p q]," +
            "s2 -> s4 [p !q], s2 -> s5 [!p q], s3 -> s6 [!p !q], s3 -> s7 [!p !q]";

    private static final String KRIPKE_LONG_ATOMS = "s1_ [!bananas !apples !kiwis] -> s2 [bananas apples !kiwis]," +
            "s1 -> s3 [bananas apples !kiwis], s2 -> s4 [bananas !apples !kiwis], s2 -> s5 [!bananas apples !kiwis]," +
            "s3 -> s6 [!bananas !apples !kiwis], s3 -> s7 [!bananas !apples kiwis]";

    private static final String UNTIL_KRIPKE = "s1 [p !q] -> s2 [p !q], s1 -> s3 [p !q], s2 -> s4 [p q]," +
            "s2 -> s5 [!p !q], s3 -> s6 [!p q], s3 -> s7 [!p q]";

    private static final String SMALL_KRIPKE = "s1 [p q !t !r] -> s2 [!p q t r], s1 -> s3 [!p !q !t !r]," +
            "s3 -> s4 [!p !q t !r], s4 -> s2, s2 -> s3";

    private static final String LARGE_KRIPKE_ONE = "s1 [!p !q] -> s2 [p !q], s2 -> s3 [p q], s3 - s4 [!p q]," +
            "s2 -> s5 [p !q], s5 -> s6 [p q], s6 - s7 [p !q], s5 -> s8 [p q], s8 -> s10 [p q], s9 [p q] - s10, s9 -> s8";

    private static final String LARGE_KRIPKE_TWO = "s1 [!c1 !c2 n1 n2 !t1 !t2] -> s2 [!c1 !c2 t1 !t2 !n1 n2]," +
            "s2 -> s3 [c1 !c2 !n1 n2 !t1 !t2], s3 -> s4 [c1 !c2 !t1 t2 !n1 !n2], s5 [!c1 !c2 t1 t2 !n1 !n2] -> s4," +
            "s2 -> s5, s4 -> s6 [!c1 !c2 n1 !n2 !t1 t2], s8 -> s2, s1 -> s6, s6 -> s7 [!c1 !c2 t1 t2 !n1 !n2]," +
            "s7 -> s8 [t1 t2 !c1 c2 !n1 !n2], s9 [n1 !n2 !t1 !t2 !c1 c2] -> s8, s6 -> s9";

    private static final String SMALL_GENERATED_KRIPKE = "s0 [!a !b !c] -> s0, s0 - s2, s1 [!a !b c] -> s2 [a !b !c]," +
            "s2 - s3 [a b !c], s2 -> s2, s3 -> s1, s3 -> s3";

    @Test
    public void testFormulaImplication() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", true,
                        "s3", true,
                        "s4", false,
                        "s5", true,
                        "s6", true,
                        "s7", true), solver.solve(CTLNode.of("p -> q")));
    }

    @Test
    public void testFormulaEquivalence() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", true,
                        "s3", true,
                        "s4", false,
                        "s5", false,
                        "s6", true,
                        "s7", true), solver.solve(CTLNode.of("p <-> q")));
    }

    @Test
    public void testNestedBooleanFormula() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", true,
                        "s3", true,
                        "s4", true,
                        "s5", false,
                        "s6", true,
                        "s7", true), solver.solve(CTLNode.of("((p | q) & q) -> p")));
    }

    @Test
    public void testFormulaWithTautology() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                        "s2", true,
                        "s3", true,
                        "s4", false,
                        "s5", true,
                        "s6",  false,
                        "s7", false), solver.solve(CTLNode.of("(p | true) & q")));
    }

    @Test
    public void testContradiction() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                        "s2", false,
                        "s3", false,
                        "s4", false,
                        "s5", false,
                        "s6",  false,
                        "s7", false), solver.solve(CTLNode.of("true & false")));
    }

    @Test
    public void testCTLExpressionEXOne() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                        "s2", true,
                        "s3", false,
                        "s4", false,
                        "s5", false,
                        "s6",  false,
                        "s7", false), solver.solve(CTLNode.of("q & EXp")));
    }

    @Test
    public void testCTLExpressionEXTwo() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", false,
                        "s3", false,
                        "s4", false,
                        "s5", false,
                        "s6", false,
                        "s7", false), solver.solve(CTLNode.of("EX(p & q)")));
    }

    @Test
    public void testFaultyExpression() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertThrows(IllegalArgumentException.class, () -> solver.solve(CTLNode.of("a &&&& b")));
    }

    @Test
    public void testFaultyCTLExpression() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertThrows(Exception.class, () -> solver.solve(CTLNode.of("EX(y)")));
    }

    @Test
    public void testDoubleNegation() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false,
                "s5", true,
                "s6", false,
                "s7", false), solver.solve(CTLNode.of("!!q")));
    }

    @Test
    public void testTripleNegation() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", true,
                "s6", true,
                "s7", true), solver.solve(CTLNode.of("!!!(p & false)")));
    }

    @Test
    public void testLongAtomNames() {
        KripkeStructure structure = Model.of(KRIPKE_LONG_ATOMS).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", true,
                "s4", false,
                "s5", false,
                "s6", false,
                "s7", false), solver.solve(CTLNode.of("EX(kiwis)")));
    }

    @Test
    public void testSimplificationOne() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false,
                "s5", false,
                "s6", false,
                "s7", false), solver.solve(CTLNode.of("(false & true) | (p & q)")));
    }

    @Test
    public void testSimplificationTwo() {
        KripkeStructure structure = Model.of(KRIPKE_LONG_ATOMS).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", false,
                "s7", true), solver.solve(CTLNode.of("!(!bananas & !kiwis)")));
    }

    @Test
    public void testCTLExpressionAXOne() {
        KripkeStructure structure = Model.of(SIMPLE_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", false,
                "s3", false,
                "s4", false,
                "s5", false,
                "s6", false,
                "s7", false), solver.solve(CTLNode.of("(q | true) & AXp")));
    }

    @Test
    public void testCTLExpressionEUOne() {
        KripkeStructure structure = Model.of(UNTIL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", true,
                "s7", true), solver.solve(CTLNode.of("E(p U q)")));
    }

    @Test
    public void testCTLExpressionAUOne() {
        KripkeStructure structure = Model.of(UNTIL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", true,
                "s7", true), solver.solve(CTLNode.of("A(p U q)")));
    }

    @Test
    public void testCTLExpressionsEGAndAG() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("EGp & AGp")));
    }

    @Test
    public void testCLTExpressionAGAndEF() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("(!AGp) | EFq")));
    }

    @Test
    public void testCTLExpressionEGImplies() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("EG(r -> t)")));
    }

    @Test
    public void testCTLExpressionAXTwo() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false), solver.solve(CTLNode.of("AX(r -> p)")));
    }

    @Test
    public void testCTLExpressionAXThree() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", true), solver.solve(CTLNode.of("AXq")));
    }

    @Test
    public void testCTLExpressionEXThree() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", false,
                "s3", false,
                "s4", true), solver.solve(CTLNode.of("EXq")));
    }

    @Test
    public void testCTLExpressionAXFour() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", false), solver.solve(CTLNode.of("!AXq")));
    }

    @Test
    public void testCTLExpressionEXFour() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false), solver.solve(CTLNode.of("!EXq")));
    }

    @Test
    public void testCTLExpressionAUTwo() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("A(p U q)")));
    }

    @Test
    public void testCTLExpressionEUTwo() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("E(p U q)")));
    }

    @Test
    public void testCTLExpressionAXAndAU() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("(AXq) & A(p U q)")));
    }

    @Test
    public void testCTLExpressionAXOrAU() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", false,
                "s4", true), solver.solve(CTLNode.of("(AXq) | A(p U q)")));
    }

    @Test
    public void testCTLExpressionEFOne() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("EFr")));
    }

    @Test
    public void testCTLExpressionAFOne() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("AFr")));
    }

    @Test
    public void testCTLExpressionEGOne() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("EGt")));
    }

    @Test
    public void testCTLExpressionAGOne() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("AGq")));
    }

    @Test
    public void testComplexCTLExpressionOne() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("AX((EFp) | (AFr))")));
    }

    @Test
    public void testComplexCTLExpressionTwo() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("EX((AFp) | (EFr))")));
    }

    @Test
    public void testComplexCTLExpressionThree() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", false,
                "s4", false), solver.solve(CTLNode.of("A(p U A(q U r))")));
    }

    @Test
    public void testComplexCTLExpressionFour() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", false,
                "s4", true), solver.solve(CTLNode.of("E(A(q U r) U t)")));
    }

    @Test
    public void testComplexCTLExpressionFive() {
        KripkeStructure structure = Model.of(SMALL_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.solve(CTLNode.of("AG(p -> A(p U (!p & A(!p U q))))")));
    }

    @Test
    public void testComplexCTLExpressionSix() {
        KripkeStructure structure = Model.of(LARGE_KRIPKE_TWO).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", true,
                "s6", true,
                "s7", true,
                "s8", true,
                "s9", true), solver.solve(CTLNode.of("AG(t1 -> (AF c1))")));
    }

    @Test
    public void testCTLExpressionEGTwo() {
        KripkeStructure structure = Model.of(LARGE_KRIPKE_ONE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", false,
                "s7", false,
                "s8", true,
                "s9", true,
                "s10", true), solver.solve(CTLNode.of("EGq")));
    }

    @Test
    public void testCTLExpressionAGTwo() {
        KripkeStructure structure = Model.of(LARGE_KRIPKE_ONE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false,
                "s5", true,
                "s6", true,
                "s7", true,
                "s8", true,
                "s9", true,
                "s10", true), solver.solve(CTLNode.of("AGp")));
    }

    @Test
    public void testComplexCTLExpressionSeven() {
        KripkeStructure structure = Model.of(SMALL_GENERATED_KRIPKE).toKripkeStructure();
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s0", false,
                "s1", false,
                "s2", true,
                "s3", true), solver.solve(CTLNode.of("EX((a & b) | !!!!c) & true")));
    }
}
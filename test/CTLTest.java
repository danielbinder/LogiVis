import org.junit.jupiter.api.Test;
import temporal.solver.CTLSolver;
import temporal.model.KripkeStructure;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CTLTest {

    /*
    Some clean string representations of valid models.

    s1, s2, s3, s4, s5, s6, s7;"
    initial: s1, s2;
    t1 : s1 - s2,
    t2 : s1 - s3,
    t3 : s2 - s4,
    t4 : s2 - s5,
    t5 : s3 - s6,
    t6 : s3 - s7;
    s1 : ,
    s2 : p q,
    s3 : p q,
    s4 : p,
    s5 : q,
    s6 : ,
    s7 : ;

    s1, s2, s3, s4, s5, s6, s7;
    initial: s1;
    t1 : s1 - s2,
    t2 : s1 - s3,
    t3 : s2 - s4,
    t4 : s2 - s5,
    t5 : s3 - s6,
    t6 : s3 - s7;
    s1 : ,
    s2 : bananas apples,
    s3 : bananas apples,
    s4 : bananas,
    s5 : apples,
    s6 : ,
    s7 : kiwis;

    s1, s2, s3, s4, s5, s6, s7;
    initial: s1, s2;
    t1 : s1 - s2,
    t2 : s1 - s3,
    t3 : s2 - s4,
    t4 : s2 - s5,
    t5 : s3 - s6,
    t6 : s3 - s7;
    s1 : ,
    s2 : p,
    s3 : p,
    s4 : p,
    s5 : ,
    s6 : ,
    s7 : ;

    // "deadlock" automaton from BMC slides of Formal Models
    0,1,2,3;
    initial: 0;
    t0: 0 - 1,
    t1: 1 - 0,
    t2: 0 - 2,
    t3: 2 - 0,
    t4: 1 - 3,
    t5: 3 - 0;
    0: ,
    1: p,
    2: q,
    3: p q;
    */

    private static final String SIMPLE_KRIPKE = "s1, s2, s3, s4, s5, s6, s7;\n" +
            "initial: s1, s2;\n" +
            "t1 : s1 - s2,\n" +
            "t2 : s1 - s3,\n" +
            "t3 : s2 - s4,\n" +
            "t4 : s2 - s5,\n" +
            "t5 : s3 - s6,\n" +
            "t6 : s3 - s7;\n" +
            "s1 : ,\n" +
            "s2 : p q,\n" +
            "s3 : p q,\n" +
            "s4 : p,\n" +
            "s5 : q,\n" +
            "s6 : ,\n" +
            "s7 : ;";

    private static final String KRIPKE_LONG_ATOMS = "s1, s2, s3, s4, s5, s6, s7;\n" +
            "initial: s1;\n" +
            "t1 : s1 - s2,\n" +
            "t2 : s1 - s3,\n" +
            "t3 : s2 - s4,\n" +
            "t4 : s2 - s5,\n" +
            "t5 : s3 - s6,\n" +
            "t6 : s3 - s7;\n" +
            "s1 : ,\n" +
            "s2 : bananas apples,\n" +
            "s3 : bananas apples,\n" +
            "s4 : bananas,\n" +
            "s5 : apples,\n" +
            "s6 : ,\n" +
            "s7 : kiwis;";

    private static final String UNTIL_KRIPKE = "s1, s2, s3, s4, s5, s6, s7;\n" +
            "initial: ;\n" +
            "t1 : s1 - s2,\n" +
            "t2 : s1 - s3,\n" +
            "t3 : s2 - s4,\n" +
            "t4 : s2 - s5,\n" +
            "t5 : s3 - s6,\n" +
            "t6 : s3 - s7;\n" +
            "s1 : p,\n" +
            "s2 : p,\n" +
            "s3 : p,\n" +
            "s4 : p q,\n" +
            "s5 : ,\n" +
            "s6 : q,\n" +
            "s7 : q;";

    private static final String SMALL_KRIPKE = "s1, s2, s3, s4;\n" +
            "initial: ;\n" +
            "t1 : s1 - s2,\n" +
            "t2 : s1 - s3,\n" +
            "t3 : s3 - s4,\n" +
            "t4 : s4 - s2,\n" +
            "t5 : s2 - s3;\n" +
            "s1 : p q,\n" +
            "s2 : q t r,\n" +
            "s3 : ,\n" +
            "s4 : t;";

    private static final String LARGE_KRIPKE_ONE = "s1, s2, s3, s4, s5, s6, s7, s8, s9, s10;\n" +
            "initial: ;\n" +
            "t1 : s1 - s2,\n" +
            "t2 : s2 - s3,\n" +
            "t3 : s3 - s4,\n" +
            "t4 : s4 - s3,\n" +
            "t5 : s2 - s5,\n" +
            "t6 : s5 - s6,\n" +
            "t7 : s6 - s7,\n" +
            "t8 : s7 - s6,\n" +
            "t9 : s5 - s8,\n" +
            "t10 : s8 - s10,\n" +
            "t11 : s9 - s10,\n" +
            "t12 : s10 - s9,\n" +
            "t13 : s9 - s8;\n" +
            "s1 : ,\n" +
            "s2 : p,\n" +
            "s3 : p q,\n" +
            "s4 : q,\n" +
            "s5 : p,\n" +
            "s6 : p q,\n" +
            "s7 : p,\n" +
            "s8 : p q,\n" +
            "s9 : p q,\n" +
            "s10 : p q;";

    private static final String LARGE_KRIPKE_TWO = "s1, s2, s3, s4, s5, s6, s7, s8, s9;\n" +
            "initial: ;\n" +
            "t1 : s1 - s2,\n" +
            "t2 : s2 - s3,\n" +
            "t3 : s3 - s4,\n" +
            "t4 : s5 - s4,\n" +
            "t5 : s2 - s5,\n" +
            "t6 : s4 - s6,\n" +
            "t7 : s8 - s2,\n" +
            "t8 : s1 - s6,\n" +
            "t9 : s6 - s7,\n" +
            "t10 : s7 - s8,\n" +
            "t11 : s9 - s8,\n" +
            "t12 : s6 - s9;\n" +
            "s1 : n1 n2 0,\n" +
            "s2 : t1 n2 1,\n" +
            "s3 : c1 n2 1,\n" +
            "s4 : c1 t2 1,\n" +
            "s5 : t1 t2 1,\n" +
            "s6 : n1 t2 2,\n" +
            "s7 : t1 t2 2,\n" +
            "s8 : t1 c2 2,\n" +
            "s9 : n1 c2 2;";

    private static final String SMALL_GENERATED_KRIPKE = "0,1,2,3;\n" +
            "initial: 2,3;\n" +
            "t0 : 0 - 0,\n" +
            "t1 : 0 - 2,\n" +
            "t2 : 1 - 2,\n" +
            "t3 : 2 - 0,\n" +
            "t4 : 2 - 3,\n" +
            "t5 : 2 - 2,\n" +
            "t6 : 3 - 1,\n" +
            "t7 : 3 - 3,\n" +
            "t8 : 3 - 2;\n" +
            "0 : ,\n" +
            "1 : c,\n" +
            "2 : a,\n" +
            "3 : a b;";

    @Test
    public void testModelParser() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        System.out.println(structure.toString());
        assertEquals("States (with atoms): " + System.lineSeparator() +
                "s1: " + System.lineSeparator() +
                "s2: p, q" + System.lineSeparator() +
                "s3: p, q" + System.lineSeparator() +
                "s4: p" + System.lineSeparator() +
                "s5: q" + System.lineSeparator() +
                "s6: " + System.lineSeparator() +
                "s7: " + System.lineSeparator() +
                "Initial state(s): s1, s2" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "t1: s1 -> s2" + System.lineSeparator() +
                "t2: s1 -> s3" + System.lineSeparator() +
                "t3: s2 -> s4" + System.lineSeparator() +
                "t4: s2 -> s5" + System.lineSeparator() +
                "t5: s3 -> s6" + System.lineSeparator() +
                "t6: s3 -> s7", structure.toString());
    }

    @Test
    public void testFormulaImplication() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                        "s2", "true",
                        "s3", "true",
                        "s4", "false",
                        "s5", "true",
                        "s6", "true",
                        "s7", "true"), solver.getSatisfyingStates("p -> q"));
    }

    @Test
    public void testFormulaEquivalence() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                        "s2", "true",
                        "s3", "true",
                        "s4", "false",
                        "s5", "false",
                        "s6", "true",
                        "s7", "true"), solver.getSatisfyingStates("p <-> q"));
    }

    @Test
    public void testNestedBooleanFormula() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                        "s2", "true",
                        "s3", "true",
                        "s4", "true",
                        "s5", "false",
                        "s6", "true",
                        "s7", "true"), solver.getSatisfyingStates("((p | q) & q) -> p"));
    }

    @Test
    public void testFormulaWithTautology() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                        "s2", "true",
                        "s3", "true",
                        "s4", "false",
                        "s5", "true",
                        "s6",  "false",
                        "s7", "false"), solver.getSatisfyingStates("(p | true) & q"));
    }

    @Test
    public void testContradiction() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                        "s2", "false",
                        "s3", "false",
                        "s4", "false",
                        "s5", "false",
                        "s6",  "false",
                        "s7", "false"), solver.getSatisfyingStates("true & false"));
    }

    @Test
    public void testCTLExpressionEXOne() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                        "s2", "true",
                        "s3", "false",
                        "s4", "false",
                        "s5", "false",
                        "s6",  "false",
                        "s7", "false"), solver.getSatisfyingStates("q & EXp"));
    }

    @Test
    public void testCTLExpressionEXTwo() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                        "s2", "false",
                        "s3", "false",
                        "s4", "false",
                        "s5", "false",
                        "s6", "false",
                        "s7", "false"), solver.getSatisfyingStates("EX(p & q)"));
    }

    @Test
    public void testFaultyExpression() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("a &&&& b", "invalid expression"), solver.getSatisfyingStates("a &&&& b"));
    }

    @Test
    public void testFaultyCTLExpression() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("EX(y)", "Unknown expression/atom/constant '(y)'."), solver.getSatisfyingStates("EX(y)"));
    }

    @Test
    public void testDoubleNegation() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "true",
                "s4", "false",
                "s5", "true",
                "s6", "false",
                "s7", "false"), solver.getSatisfyingStates("!!q"));
    }

    @Test
    public void testTripleNegation() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true",
                "s5", "true",
                "s6", "true",
                "s7", "true"), solver.getSatisfyingStates("!!!(p & false)"));
    }

    @Test
    public void testLongAtomNames() {
        KripkeStructure structure = new KripkeStructure(KRIPKE_LONG_ATOMS);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "true",
                "s4", "false",
                "s5", "false",
                "s6", "false",
                "s7", "false"), solver.getSatisfyingStates("EX(kiwis)"));
    }

    @Test
    public void testSimplificationOne() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "true",
                "s4", "false",
                "s5", "false",
                "s6", "false",
                "s7", "false"), solver.getSatisfyingStates("(false & true) | (p & q)"));
    }

    @Test
    public void testSimplificationTwo() {
        KripkeStructure structure = new KripkeStructure(KRIPKE_LONG_ATOMS);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "true",
                "s4", "true",
                "s5", "false",
                "s6", "false",
                "s7", "true"), solver.getSatisfyingStates("!(!bananas & !kiwis)"));
    }

    @Test
    public void testCTLExpressionAXOne() {
        KripkeStructure structure = new KripkeStructure(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "false",
                "s3", "false",
                "s4", "false",
                "s5", "false",
                "s6", "false",
                "s7", "false"), solver.getSatisfyingStates("(q | true) & AXp"));
    }

    @Test
    public void testCTLExpressionEUOne() {
        KripkeStructure structure = new KripkeStructure(UNTIL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true",
                "s5", "false",
                "s6", "true",
                "s7", "true"), solver.getSatisfyingStates("E(p U q)"));
    }

    @Test
    public void testCTLExpressionAUOne() {
        KripkeStructure structure = new KripkeStructure(UNTIL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "true",
                "s4", "true",
                "s5", "false",
                "s6", "true",
                "s7", "true"), solver.getSatisfyingStates("A(p U q)"));
    }

    @Test
    public void testCTLExpressionsEGAndAG() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("EGp & AGp"));
    }

    @Test
    public void testCLTExpressionAGAndEF() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("(!AGp) | EFq"));
    }

    @Test
    public void testCTLExpressionEGImplies() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("EG(r -> t)"));
    }

    @Test
    public void testCTLExpressionAXTwo() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "true",
                "s4", "false"), solver.getSatisfyingStates("AX(r -> p)"));
    }

    @Test
    public void testCTLExpressionAXThree() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "false",
                "s4", "true"), solver.getSatisfyingStates("AXq"));
    }

    @Test
    public void testCTLExpressionEXThree() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "false",
                "s3", "false",
                "s4", "true"), solver.getSatisfyingStates("EXq"));
    }

    @Test
    public void testCTLExpressionAXFour() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "false"), solver.getSatisfyingStates("!AXq"));
    }

    @Test
    public void testCTLExpressionEXFour() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "true",
                "s4", "false"), solver.getSatisfyingStates("!EXq"));
    }

    @Test
    public void testCTLExpressionAUTwo() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("A(p U q)"));
    }

    @Test
    public void testCTLExpressionEUTwo() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("E(p U q)"));
    }

    @Test
    public void testCTLExpressionAXAndAU() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("AXq & A(p U q)"));
    }

    @Test
    public void testCTLExpressionAXOrAU() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "false",
                "s4", "true"), solver.getSatisfyingStates("AXq | A(p U q)"));
    }

    @Test
    public void testCTLExpressionEFOne() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("EFr"));
    }

    @Test
    public void testCTLExpressionAFOne() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("AFr"));
    }

    @Test
    public void testCTLExpressionEGOne() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("EGt"));
    }

    @Test
    public void testCTLExpressionAGOne() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("AGq"));
    }

    @Test
    public void testComplexCTLExpressionOne() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("AX((EFp) | (AFr))"));
    }

    @Test
    public void testComplexCTLExpressionTwo() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("EX((AFp) | (EFr))"));
    }

    @Test
    public void testComplexCTLExpressionThree() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "false",
                "s4", "false"), solver.getSatisfyingStates("A(p U A(q U r))"));
    }

    @Test
    public void testComplexCTLExpressionFour() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "true",
                "s3", "false",
                "s4", "true"), solver.getSatisfyingStates("E(A(q U r) U t)"));
    }

    @Test
    public void testComplexCTLExpressionFive() {
        KripkeStructure structure = new KripkeStructure(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true"), solver.getSatisfyingStates("AG(p -> A(p U (!p & A(!p U q))))"));
    }

    @Test
    public void testComplexCTLExpressionSix() {
        KripkeStructure structure = new KripkeStructure(LARGE_KRIPKE_TWO);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "true",
                "s2", "true",
                "s3", "true",
                "s4", "true",
                "s5", "true",
                "s6", "true",
                "s7", "true",
                "s8", "true",
                "s9", "true"), solver.getSatisfyingStates("AG(t1 -> (AF c1))"));
    }

    @Test
    public void testCTLExpressionEGTwo() {
        KripkeStructure structure = new KripkeStructure(LARGE_KRIPKE_ONE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "true",
                "s4", "true",
                "s5", "false",
                "s6", "false",
                "s7", "false",
                "s8", "true",
                "s9", "true",
                "s10", "true"), solver.getSatisfyingStates("EGq"));
    }

    @Test
    public void testCTLExpressionAGTwo() {
        KripkeStructure structure = new KripkeStructure(LARGE_KRIPKE_ONE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", "false",
                "s2", "false",
                "s3", "false",
                "s4", "false",
                "s5", "true",
                "s6", "true",
                "s7", "true",
                "s8", "true",
                "s9", "true",
                "s10", "true"), solver.getSatisfyingStates("AGp"));
    }

    @Test
    public void testComplexCTLExpressionSeven() {
        KripkeStructure structure = new KripkeStructure(SMALL_GENERATED_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("0", "false",
                "1", "false",
                "2", "true",
                "3", "true"), solver.getSatisfyingStates("EX((a & b) | !!!!c) & true"));
    }
}
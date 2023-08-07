import org.junit.jupiter.api.Test;
import temporal.solver.CTLSolver;
import temporal.model.KripkeStruct;

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

    private static final String SIMPLE_KRIPKE = """
            s1, s2, s3, s4, s5, s6, s7;
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
            s7 : ;""";

    private static final String KRIPKE_LONG_ATOMS = """
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
            s7 : kiwis;""";

    private static final String UNTIL_KRIPKE = """
            s1, s2, s3, s4, s5, s6, s7;
            initial: ;
            t1 : s1 - s2,
            t2 : s1 - s3,
            t3 : s2 - s4,
            t4 : s2 - s5,
            t5 : s3 - s6,
            t6 : s3 - s7;
            s1 : p,
            s2 : p,
            s3 : p,
            s4 : p q,
            s5 : ,
            s6 : q,
            s7 : q;""";

    private static final String SMALL_KRIPKE = """
            s1, s2, s3, s4;
            initial: ;
            t1 : s1 - s2,
            t2 : s1 - s3,
            t3 : s3 - s4,
            t4 : s4 - s2,
            t5 : s2 - s3;
            s1 : p q,
            s2 : q t r,
            s3 : ,
            s4 : t;""";

    private static final String LARGE_KRIPKE_ONE = """
            s1, s2, s3, s4, s5, s6, s7, s8, s9, s10;
            initial: ;
            t1 : s1 - s2,
            t2 : s2 - s3,
            t3 : s3 - s4,
            t4 : s4 - s3,
            t5 : s2 - s5,
            t6 : s5 - s6,
            t7 : s6 - s7,
            t8 : s7 - s6,
            t9 : s5 - s8,
            t10 : s8 - s10,
            t11 : s9 - s10,
            t12 : s10 - s9,
            t13 : s9 - s8;
            s1 : ,
            s2 : p,
            s3 : p q,
            s4 : q,
            s5 : p,
            s6 : p q,
            s7 : p,
            s8 : p q,
            s9 : p q,
            s10 : p q;""";

    private static final String LARGE_KRIPKE_TWO = """
            s1, s2, s3, s4, s5, s6, s7, s8, s9;
            initial: ;
            t1 : s1 - s2,
            t2 : s2 - s3,
            t3 : s3 - s4,
            t4 : s5 - s4,
            t5 : s2 - s5,
            t6 : s4 - s6,
            t7 : s8 - s2,
            t8 : s1 - s6,
            t9 : s6 - s7,
            t10 : s7 - s8,
            t11 : s9 - s8,
            t12 : s6 - s9;
            s1 : n1 n2 0,
            s2 : t1 n2 1,
            s3 : c1 n2 1,
            s4 : c1 t2 1,
            s5 : t1 t2 1,
            s6 : n1 t2 2,
            s7 : t1 t2 2,
            s8 : t1 c2 2,
            s9 : n1 c2 2;""";

    private static final String SMALL_GENERATED_KRIPKE = """
            0,1,2,3;
            initial: 2,3;
            t0 : 0 - 0,
            t1 : 0 - 2,
            t2 : 1 - 2,
            t3 : 2 - 0,
            t4 : 2 - 3,
            t5 : 2 - 2,
            t6 : 3 - 1,
            t7 : 3 - 3,
            t8 : 3 - 2;
            0 : ,
            1 : c,
            2 : a,
            3 : a b;""";

    @Test
    public void testModelParser() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        System.out.println(structure);
        assertEquals("""
                States (with atoms):\s
                s1:\s
                s2: p, q
                s3: p, q
                s4: p
                s5: q
                s6:\s
                s7:\s
                Initial state(s): s1, s2
                Transitions:\s
                t1: s1 -> s2
                t2: s1 -> s3
                t3: s2 -> s4
                t4: s2 -> s5
                t5: s3 -> s6
                t6: s3 -> s7""",
                structure.toString());
    }

    @Test
    public void testFormulaImplication() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", true,
                        "s3", true,
                        "s4", false,
                        "s5", true,
                        "s6", true,
                        "s7", true), solver.getSatisfyingStates("p -> q").solverResult);
    }

    @Test
    public void testFormulaEquivalence() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", true,
                        "s3", true,
                        "s4", false,
                        "s5", false,
                        "s6", true,
                        "s7", true), solver.getSatisfyingStates("p <-> q").solverResult);
    }

    @Test
    public void testNestedBooleanFormula() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", true,
                        "s3", true,
                        "s4", true,
                        "s5", false,
                        "s6", true,
                        "s7", true), solver.getSatisfyingStates("((p | q) & q) -> p").solverResult);
    }

    @Test
    public void testFormulaWithTautology() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                        "s2", true,
                        "s3", true,
                        "s4", false,
                        "s5", true,
                        "s6",  false,
                        "s7", false), solver.getSatisfyingStates("(p | true) & q").solverResult);
    }

    @Test
    public void testContradiction() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                        "s2", false,
                        "s3", false,
                        "s4", false,
                        "s5", false,
                        "s6",  false,
                        "s7", false), solver.getSatisfyingStates("true & false").solverResult);
    }

    @Test
    public void testCTLExpressionEXOne() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                        "s2", true,
                        "s3", false,
                        "s4", false,
                        "s5", false,
                        "s6",  false,
                        "s7", false), solver.getSatisfyingStates("q & EXp").solverResult);
    }

    @Test
    public void testCTLExpressionEXTwo() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                        "s2", false,
                        "s3", false,
                        "s4", false,
                        "s5", false,
                        "s6", false,
                        "s7", false), solver.getSatisfyingStates("EX(p & q)").solverResult);
    }

    @Test
    public void testFaultyExpression() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals("invalid expression", solver.getSatisfyingStates("a &&&& b").errorMessage);
    }

    @Test
    public void testFaultyCTLExpression() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals("Unknown expression/atom/constant '(y)'.", solver.getSatisfyingStates("EX(y)").errorMessage);
    }

    @Test
    public void testDoubleNegation() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false,
                "s5", true,
                "s6", false,
                "s7", false), solver.getSatisfyingStates("!!q").solverResult);
    }

    @Test
    public void testTripleNegation() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", true,
                "s6", true,
                "s7", true), solver.getSatisfyingStates("!!!(p & false)").solverResult);
    }

    @Test
    public void testLongAtomNames() {
        KripkeStruct structure = new KripkeStruct(KRIPKE_LONG_ATOMS);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", true,
                "s4", false,
                "s5", false,
                "s6", false,
                "s7", false), solver.getSatisfyingStates("EX(kiwis)").solverResult);
    }

    @Test
    public void testSimplificationOne() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false,
                "s5", false,
                "s6", false,
                "s7", false), solver.getSatisfyingStates("(false & true) | (p & q)").solverResult);
    }

    @Test
    public void testSimplificationTwo() {
        KripkeStruct structure = new KripkeStruct(KRIPKE_LONG_ATOMS);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", false,
                "s7", true), solver.getSatisfyingStates("!(!bananas & !kiwis)").solverResult);
    }

    @Test
    public void testCTLExpressionAXOne() {
        KripkeStruct structure = new KripkeStruct(SIMPLE_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", false,
                "s3", false,
                "s4", false,
                "s5", false,
                "s6", false,
                "s7", false), solver.getSatisfyingStates("(q | true) & AXp").solverResult);
    }

    @Test
    public void testCTLExpressionEUOne() {
        KripkeStruct structure = new KripkeStruct(UNTIL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", true,
                "s7", true), solver.getSatisfyingStates("E(p U q)").solverResult);
    }

    @Test
    public void testCTLExpressionAUOne() {
        KripkeStruct structure = new KripkeStruct(UNTIL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", true,
                "s4", true,
                "s5", false,
                "s6", true,
                "s7", true), solver.getSatisfyingStates("A(p U q)").solverResult);
    }

    @Test
    public void testCTLExpressionsEGAndAG() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("EGp & AGp").solverResult);
    }

    @Test
    public void testCLTExpressionAGAndEF() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("(!AGp) | EFq").solverResult);
    }

    @Test
    public void testCTLExpressionEGImplies() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("EG(r -> t)").solverResult);
    }

    @Test
    public void testCTLExpressionAXTwo() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false), solver.getSatisfyingStates("AX(r -> p)").solverResult);
    }

    @Test
    public void testCTLExpressionAXThree() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", true), solver.getSatisfyingStates("AXq").solverResult);
    }

    @Test
    public void testCTLExpressionEXThree() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", false,
                "s3", false,
                "s4", true), solver.getSatisfyingStates("EXq").solverResult);
    }

    @Test
    public void testCTLExpressionAXFour() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", false), solver.getSatisfyingStates("!AXq").solverResult);
    }

    @Test
    public void testCTLExpressionEXFour() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", true,
                "s4", false), solver.getSatisfyingStates("!EXq").solverResult);
    }

    @Test
    public void testCTLExpressionAUTwo() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("A(p U q)").solverResult);
    }

    @Test
    public void testCTLExpressionEUTwo() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("E(p U q)").solverResult);
    }

    @Test
    public void testCTLExpressionAXAndAU() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("AXq & A(p U q)").solverResult);
    }

    @Test
    public void testCTLExpressionAXOrAU() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", false,
                "s4", true), solver.getSatisfyingStates("AXq | A(p U q)").solverResult);
    }

    @Test
    public void testCTLExpressionEFOne() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("EFr").solverResult);
    }

    @Test
    public void testCTLExpressionAFOne() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("AFr").solverResult);
    }

    @Test
    public void testCTLExpressionEGOne() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("EGt").solverResult);
    }

    @Test
    public void testCTLExpressionAGOne() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", false,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("AGq").solverResult);
    }

    @Test
    public void testComplexCTLExpressionOne() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("AX((EFp) | (AFr))").solverResult);
    }

    @Test
    public void testComplexCTLExpressionTwo() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("EX((AFp) | (EFr))").solverResult);
    }

    @Test
    public void testComplexCTLExpressionThree() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", false,
                "s4", false), solver.getSatisfyingStates("A(p U A(q U r))").solverResult);
    }

    @Test
    public void testComplexCTLExpressionFour() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", false,
                "s2", true,
                "s3", false,
                "s4", true), solver.getSatisfyingStates("E(A(q U r) U t)").solverResult);
    }

    @Test
    public void testComplexCTLExpressionFive() {
        KripkeStruct structure = new KripkeStruct(SMALL_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true), solver.getSatisfyingStates("AG(p -> A(p U (!p & A(!p U q))))").solverResult);
    }

    @Test
    public void testComplexCTLExpressionSix() {
        KripkeStruct structure = new KripkeStruct(LARGE_KRIPKE_TWO);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("s1", true,
                "s2", true,
                "s3", true,
                "s4", true,
                "s5", true,
                "s6", true,
                "s7", true,
                "s8", true,
                "s9", true), solver.getSatisfyingStates("AG(t1 -> (AF c1))").solverResult);
    }

    @Test
    public void testCTLExpressionEGTwo() {
        KripkeStruct structure = new KripkeStruct(LARGE_KRIPKE_ONE);
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
                "s10", true), solver.getSatisfyingStates("EGq").solverResult);
    }

    @Test
    public void testCTLExpressionAGTwo() {
        KripkeStruct structure = new KripkeStruct(LARGE_KRIPKE_ONE);
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
                "s10", true), solver.getSatisfyingStates("AGp").solverResult);
    }

    @Test
    public void testComplexCTLExpressionSeven() {
        KripkeStruct structure = new KripkeStruct(SMALL_GENERATED_KRIPKE);
        CTLSolver solver = new CTLSolver(structure);
        assertEquals(Map.of("0", false,
                "1", false,
                "2", true,
                "3", true), solver.getSatisfyingStates("EX((a & b) | !!!!c) & true").solverResult);
    }
}
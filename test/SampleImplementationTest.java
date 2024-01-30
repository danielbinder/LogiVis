import model.parser.Model;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SampleImplementationTest {
    @Test
    public void testIsDeterministic() {
        assertTrue(Model.of("s1_ -> [a] s2, s1 -> [b] s3")
                           .toFiniteAutomaton()
                           .isDeterministic());

        assertTrue(Model.of("s1_ -> [a] s2, s1 -> [b] s2, s2 -> [a] s3, s3 -> [a] s4")
                           .toFiniteAutomaton()
                           .isDeterministic());

        assertFalse(Model.of("s1_ -> [a] s2, s1 -> [a] s3")
                            .toFiniteAutomaton()
                            .isDeterministic());

        assertFalse(Model.of("s1_ -> [a] s3, s2_ -> [a] s3, s3 -> [a] s1")
                            .toFiniteAutomaton()
                            .isDeterministic());

        assertFalse(Model.of("s1_ -> [a] s2, s2 -> [a] s3, s3 -> [b] s4, s3 -> [b] s5")
                            .toFiniteAutomaton()
                            .isDeterministic());
    }

    @Test
    public void testIsComplete() {
        assertTrue(Model.of("s1_ -> [a] s2, s2 -> [a] s3, s3 -> [a] s1")
                           .toFiniteAutomaton()
                           .isComplete());

        assertTrue(Model.of("s1_ -> [a] s2, s1 -> [a] s3, s1 -> [b] s2, s2 -> [a] s3, s2 -> [b] s1, s3 -> [a] s1, s3 -> [b] s2")
                           .toFiniteAutomaton()
                           .isComplete());

        assertFalse(Model.of("s1_ -> [a b] s2, s2 -> [a] s1")
                            .toFiniteAutomaton()
                            .isComplete());

        assertFalse(Model.of("s1_ -> [a b] s2, s1 -> [a] s3, s2 -> [b] s3")
                            .toFiniteAutomaton()
                            .isComplete());
    }

    @Test
    public void testIsEquivalent() {
        assertTrue(Model.of("q1_* -> [c] q1, q1 - [d] q2, q2 - [c] q3, q3 -> [d] q3")
                           .toFiniteAutomaton()
                           .isEquivalent(
                                   Model.of("q4_* -> [c] q4, q4 - [d] q5, q5 -> [c] q6, q6 -> [d] q6, q6 - [c] q7, q7 -> [d] q4")
                                           .toFiniteAutomaton()));

        assertTrue(Model.of("""
                                    S = {s0}
                                    I = {s0}
                                    T = {(s0, s0) [as0]}
                                    F = {s0}""")
                           .toFiniteAutomaton()
                           .isEquivalent(
                                   Model.of("""
                                    S = {s0}
                                    I = {s0}
                                    T = {(s0, s0) [as0]}
                                    F = {s0}""")
                                           .toFiniteAutomaton()));

        assertFalse(Model.of("q1_* -> [c] q1, q1 - [d] q2, q2 - [c] q3, q3 -> [d] q3")
                            .toFiniteAutomaton()
                            .isEquivalent(
                                    Model.of("q4_* -> [c] q4, q4 -> [d] q5, q5 -> [d] q6, q6 -> [d] q6, q6 - [c] q7, q7 -> [c] q4, q5 - [c] q7")
                                            .toFiniteAutomaton()));
    }

    @Test
    public void testIsSimulatedBy() {
        assertTrue(Model.of("s1_ -> [c] s3, s1 -> [c] s2, s3 -> [d] s5 , s2 -> [m] s4")
                .toFiniteAutomaton()
                .isSimulatedBy(
                        Model.of("s6_ -> [c] s7, s7 -> [d] s9, s7 -> [m] s8")
                                .toFiniteAutomaton()
                ));

        assertFalse(Model.of("s6_ -> [c] s7, s7 -> [d] s9, s7 -> [m] s8")
                .toFiniteAutomaton()
                .isSimulatedBy(
                        Model.of("s1_ -> [c] s3, s1 -> [c] s2, s3 -> [d] s5 , s2 -> [m] s4")
                                .toFiniteAutomaton()
                ));

        assertTrue(Model.of("s4_ -> [a] s5, s4 -> [a] s6, s6 -> [c] s6, s5 -> [b c] s5")
                .toFiniteAutomaton()
                .isSimulatedBy(
                        Model.of("s1_ -> [a] s2, s1 -> [a] s3, s3 -> [b] s3, s2 -> [b c] s2")
                                .toFiniteAutomaton()
                ));

        assertTrue(Model.of("s1_ -> [a] s2, s1 -> [a] s3, s3 -> [b] s3, s2 -> [b c] s2")
                .toFiniteAutomaton()
                .isSimulatedBy(
                        Model.of("s4_ -> [a] s5, s4 -> [a] s6, s6 -> [c] s6, s5 -> [b c] s5")
                                .toFiniteAutomaton()
                ));
    }

    @Test
    public void testAreReachable() {
        assertTrue(Model.of("s1_ -> [a] s2, s2 -> [b] s3, s3 -> [c] s4<")
                           .toFiniteAutomaton()
                           .areReachable());

        assertTrue(Model.of("""
                                    S = {s0, s5<, s4, s8, s1, s7, s3, s6<, s2}
                                    I = {s0, s2}
                                    T = {(s0, s4) [c], (s0, s1) [b], (s5, s0) [b], (s4, s8) [c], (s4, s7) [c], (s8, s3) [b], (s1, s1) [a], (s7, s7) [a], (s7, s6) [c], (s3, s5) [b], (s3, s1) [a], (s6, s0) [a], (s2, s7) [a], (s2, s3) [a]}
                                    F = {s0, s3}""")
                           .toFiniteAutomaton()
                           .areReachable());

        assertFalse(Model.of("s1_ -> [a] s2, s2 -> [b] s3, s4<")
                            .toFiniteAutomaton()
                            .areReachable());

        assertFalse(Model.of("""
                                     S = {s0, s5<, s4, s8, s1, s7, s3, s6<, s2, s9, s10<}
                                     I = {s0, s2}
                                     T = {(s0, s4) [c], (s0, s1) [b], (s5, s0) [b], (s4, s8) [c], (s4, s7) [c], (s8, s3) [b], (s1, s1) [a], (s7, s7) [a], (s7, s6) [c], (s3, s5) [b], (s3, s1) [a], (s6, s0) [a], (s2, s7) [a], (s2, s3) [a], (s9, s10) [a], (s10, s9) [b]}
                                     F = {s0, s3}""")
                            .toFiniteAutomaton()
                            .areReachable());
    }

    @Test
    public void testToProductAutomaton() {
        assertEquals(Model.of("s0_ -> [a] s1, s1 -> [b] s2*, s2 -> [a b] s2")
                             .toFiniteAutomaton()
                             .toProductAutomaton(Model.of("t0_ -> [a b] t0, t0 -> [b] t1, t1 -> [a] t2*")
                                                         .toFiniteAutomaton())
                             .toModel(),
                     Model.of("""
                                      S = {s1t0, s2t0, s2t2, s0t0, s2t1}
                                      I = {s0t0}
                                      T = {(s1t0, s2t0) [b], (s1t0, s2t1) [b], (s2t0, s2t0) [a b], (s2t0, s2t1) [b], (s0t0, s1t0) [a], (s2t1, s2t2) [a]}
                                      F = {s2t2}"""));

        assertEquals(Model.of("s0_ -> [a] s1, s1 -> [b] s2*, s2 -> [a b] s2")
                             .toFiniteAutomaton()
                             .toProductAutomaton(Model.of("t0_ -> [a] t1_")
                                                         .toFiniteAutomaton())
                             .toModel(),
                     Model.of("""
                                      S = {s0t0, s1t1, s0t1}
                                      I = {s0t0, s0t1}
                                      T = {(s0t0, s1t1) [a]}"""));
    }

    @Test
    public void testToPowerAutomaton() {
        assertEquals(Model.of("s0_ -> [a b] s0, s0 -> [a] s1_*")
                             .toFiniteAutomaton()
                             .toPowerAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s0, s0s1}
                                      I = {s0s1}
                                      T = {(s0, s0) [b], (s0, s0s1) [a], (s0s1, s0) [b], (s0s1, s0s1) [a]}
                                      F = {s0s1}"""));

        assertEquals(Model.of("s0_ -> [a] s1, s1 -> [b] s2, s1 -> [b] s3, s2 -> [a b] s2, s2 -> [b] s3, s3 -> [a] s4*")
                             .toFiniteAutomaton()
                             .toPowerAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s0, s1, s2, sink, s2s3, s2s4}
                                      I = {s0}
                                      T = {(s0, sink) [b], (s0, s1) [a], (s1, sink) [a], (s1, s2s3) [b], (s2, s2s3) [b],
                                      (s2, s2) [a], (sink, sink) [a b], (s2s3, s2s3) [b], (s2s3, s2s4) [a],
                                      (s2s4, s2s3) [b], (s2s4, s2) [a]}
                                      F = {s2s4}"""));

        assertEquals(Model.of("""
                                      s0_* -> [b0] s1_, s1 -> [a0 b0] s1, s0 -> [a0 b2] s3, s0 -> [b1] s2*,
                                      s2 -> [b0] s1, s3 -> [a0] s2, s2 -> [a0] s4*, s1 -> [b1] s4,
                                      s3 -> [b0] s4, s4 -> [a0 b0] s4""")
                             .toFiniteAutomaton()
                             .toPowerAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s3, s1, s1s4, s1s2, s1s3, s2, s2s4, sink, s4, s0s1}
                                      I = {s0s1}
                                      T = {(s3, sink) [b2 b1], (s3, s4) [b0], (s3, s2) [a0], (s1, sink) [b2],
                                      (s1, s4) [b1], (s1, s1) [b0 a0], (s1s4, sink) [b2], (s1s4, s1s4) [b0 a0],
                                      (s1s4, s4) [b1], (s1s2, sink) [b2], (s1s2, s1s4) [a0], (s1s2, s4) [b1],
                                      (s1s2, s1) [b0], (s1s3, sink) [b2], (s1s3, s1s4) [b0], (s1s3, s1s2) [a0],
                                      (s1s3, s4) [b1], (s2, sink) [b2 b1], (s2, s4) [a0], (s2, s1) [b0],
                                      (s2s4, sink) [b2 b1], (s2s4, s1s4) [b0], (s2s4, s4) [a0],
                                      (sink, sink) [b2 b0 a0 b1], (s4, sink) [b2 b1], (s4, s4) [b0 a0],
                                      (s0s1, s3) [b2], (s0s1, s1) [b0], (s0s1, s1s3) [a0], (s0s1, s2s4) [b1]}
                                      F = {s1s4, s1s2, s2, s2s4, s4, s0s1}"""));
    }

    @Test
    public void testToComplementAutomaton() {
        assertEquals(Model.of("s0_ -> [a] s1, s1 -> [a] s2*, s0 -> [a] s4*")
                             .toFiniteAutomaton()
                             .toComplementAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {sink, s1s4, s0, s2}
                                      I = {s0}
                                      T = {(sink, sink) [a], (s1s4, s2) [a], (s0, s1s4) [a], (s2, sink) [a]}
                                      F = {sink, s0}
                                      """));

        assertEquals(Model.of("""
                                      s0_ -> [a] s1, s0 -> [b] sink, s1 -> [a] sink, sink -> [a b] sink,
                                      s1 -> [b] s2s3, s2s3 -> [b] s2s3, s2s3 -> [a] s2s4*, s2s4 -> [b] s2s3,
                                      s2s4 -> [a] s2, s2 -> [a] s2, s2 -> [b] s2s3""")
                             .toFiniteAutomaton()
                             .toComplementAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s2, sink, s0, s1, s2s4, s2s3}
                                      I = {s0}
                                      T = {(s2, s2s3) [b], (s2, s2) [a], (sink, sink) [a b], (s0, sink) [b], (s0, s1) [a], (s1, sink) [a], (s1, s2s3) [b], (s2s4, s2s3) [b], (s2s4, s2) [a], (s2s3, s2s4) [a], (s2s3, s2s3) [b]}
                                      F = {s2, sink, s0, s1, s2s3}"""));
    }

    @Test
    public void testToSinkAutomaton() {
        assertEquals(Model.of("s1 -> [a] s2")
                             .toFiniteAutomaton()
                             .toSinkAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s1, sink, s2}
                                      T = {(s1, s2) [a], (sink, sink) [a], (s2, sink) [a]}
                                      """));

        assertEquals(Model.of("s1_ -> [a b] s2, s1 -> [a] s3, s2 -> [b] s3")
                             .toFiniteAutomaton()
                             .toSinkAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s3, s2, sink, s1}
                                      I = {s1}
                                      T = {(s3, sink) [a b], (s2, sink) [a], (s2, s3) [b], (sink, sink) [a b], (s1, s3) [a], (s1, s2) [a b]}"""));
    }

    @Test
    public void testToOracleAutomaton() {
        assertEquals(Model.of("""
                                      s0_* -> [b] s1_, s1 -> [a b] s1, s0 -> [a b] s3, s0 -> [b] s2*, s2 -> [b] s1,
                                      s3 -> [a] s2, s2 -> [a] s4*, s1 -> [b] s4, s3 -> [b] s4, s4 -> [a b] s4""")
                             .toFiniteAutomaton()
                             .toOracleAutomaton()
                             .toModel(),
                     Model.of("""
                                      S = {s4, s3, s1, s2, s0}
                                      I = {s1, s0}
                                      T = {(s4, s4) [bs4 as4], (s3, s4) [bs4], (s3, s2) [as2], (s1, s4) [bs4], (s1, s1) [bs1 as1], (s2, s4) [as4], (s2, s1) [bs1], (s0, s3) [as3 bs3], (s0, s1) [bs1], (s0, s2) [bs2]}
                                      F = {s4, s2, s0}"""));
    }

    @Disabled("Can fail, since Sets are per definition not in any order.\n" +
            "Therefore, properties can and will get numbered in different orders.")
    @Test
    public void testToOptimisedOracleAutomaton() {
        assertEquals(Model.of("""
                                      s0_* -> [b] s1_, s1 -> [a b] s1, s0 -> [a b] s3, s0 -> [b] s2*, s2 -> [b] s1,
                                      s3 -> [a] s2, s2 -> [a] s4*, s1 -> [b] s4, s3 -> [b] s4, s4 -> [a b] s4""")
                           .toFiniteAutomaton()
                           .toOptimisedOracleAutomaton()
                           .toModel(),
                   Model.of("""
                                      S = {s3, s0, s2, s4, s1}
                                      I = {s0, s1}
                                      T = {(s3, s4) [b0], (s3, s2) [a0], (s0, s3) [b0 a0], (s0, s1) [b2], (s0, s2) [b1], (s2, s4) [a0], (s2, s1) [b0], (s4, s4) [b0 a0], (s1, s4) [b0], (s1, s1) [a0 b1]}
                                      F = {s0, s2, s4}"""));
    }
}

package model.variant.finite.interpreter;

import model.parser.Model;
import model.variant.finite.FiniteAutomaton;
import model.variant.finite.FiniteAutomatonGenerator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static marker.AlgorithmImplementation.SAMPLE;
import static marker.AlgorithmImplementation.USER;

public class ImplementationValidator {
    private static final Function<String, FiniteAutomaton> automatonFunction = s -> Model.of(s).toFiniteAutomaton();
    private TestReport<String, FiniteAutomaton> testReport;

    public String validateAll(String name, boolean compact) {
        testReport = new TestReport<>(FiniteAutomaton::isEquivalent);

        if(isImplemented(() -> USER.isDeterministic(automaton("a")))) validateIsDeterministic();
        if(isImplemented(() -> USER.isComplete(automaton("a")))) validateIsComplete();
        if(isImplemented(() -> USER.isEquivalent(automaton("a"), automaton("b")))) validateIsEquivalent();
        if(isImplemented(() -> USER.areReachable(automaton("a")))) validateAreReachable();
        if(isImplemented(() -> USER.toProductAutomaton(automaton("a"), automaton("b")))) validateToProductAutomaton();
        if(isImplemented(() -> USER.toPowerAutomaton(automaton("a")))) validateToPowerAutomaton();
        if(isImplemented(() -> USER.toComplementAutomaton(automaton("a")))) validateToComplementAutomaton();
        if(isImplemented(() -> USER.toSinkAutomaton(automaton("a")))) validateToSinkAutomaton();
        if(isImplemented(() -> USER.toOracleAutomaton(automaton("a")))) validateToOracleAutomaton();
        if(isImplemented(() -> USER.toOptimisedOracleAutomaton(automaton("a")))) validateToOptimisedOracleAutomaton();
        if(isImplemented(() -> USER.isSimulatedBy(automaton("a"), automaton("b")))) validateIsSimulatedBy();

        TestReportFile.compile(testReport.compile(name, false), name);
        return testReport.compile(name, compact);
    }

    public String validate(String method, String name, boolean compact) {
        testReport = new TestReport<>(FiniteAutomaton::isEquivalent);

        switch(method) {
            case "isDeterministic" -> validateIsDeterministic();
            case "isComplete" -> validateIsComplete();
            case "isEquivalent" -> validateIsEquivalent();
            case "areReachable" -> validateAreReachable();
            case "toProductAutomaton" -> validateToProductAutomaton();
            case "toPowerAutomaton" -> validateToPowerAutomaton();
            case "toComplementAutomaton" -> validateToComplementAutomaton();
            case "toSinkAutomaton" -> validateToSinkAutomaton();
            case "toOracleAutomaton" -> validateToOracleAutomaton();
            case "toOptimisedOracleAutomaton" -> validateToOptimisedOracleAutomaton();
            case "isSimulatedBy" -> validateIsSimulatedBy();
        }

        return testReport.compile(name, compact);
    }

    private void validateIsDeterministic() {
        String testName = "isDeterministic";

        testReport.testTrue(testName,
                            automatonFunction.andThen(USER::isDeterministic),
                            List.of("s1_ -> [a] s2, s1 -> [b] s3",
                                    "s1_ -> [a] s2, s1 -> [b] s2, s2 -> [a] s3, s3 -> [a] s4"));

        testReport.testFalse(testName,
                             automatonFunction.andThen(USER::isDeterministic),
                             List.of("s1_ -> [a] s2, s1 -> [a] s3",
                                     "s1_ -> [a] s3, s2_ -> [a] s3, s3 -> [a] s1",
                                     "s1_ -> [a] s2, s2 -> [a] s3, s3 -> [b] s4, s3 -> [b] s5"));

        testReport.sectionDivider(testName);

        testReport.compareBoolean(testName,
                                  automatonFunction.andThen(USER::isDeterministic),
                                  automatonFunction.andThen(SAMPLE::isDeterministic),
                                  generateAutomatons());
    }

    private void validateIsComplete() {
        String testName = "isComplete";

        testReport.testTrue(testName,
                            automatonFunction.andThen(USER::isComplete),
                            List.of("s1_ -> [a] s2, s2 -> [a] s3, s3 -> [a] s1",
                                    "s1_ -> [a] s2, s1 -> [a] s3, s1 -> [b] s2, s2 -> [a] s3, s2 -> [b] s1, s3 -> [a] s1, s3 -> [b] s2"));

        testReport.testFalse(testName,
                             automatonFunction.andThen(USER::isComplete),
                             List.of("s1_ -> [a b] s2, s2 -> [a] s1",
                                     "s1_ -> [a b] s2, s1 -> [a] s3, s2 -> [b] s3"));

        testReport.sectionDivider(testName);

        testReport.compareBoolean(testName,
                                  automatonFunction.andThen(USER::isComplete),
                                  automatonFunction.andThen(SAMPLE::isComplete),
                                  generateAutomatons());
    }

    private void validateIsEquivalent() {
        String testName = "isEquivalent";

        testReport.testTrue(testName,
                        (i1, i2) -> USER.isEquivalent(automaton(i1), automaton(i2)),
                        List.of("q1_* -> [c] q1, q1 - [d] q2, q2 - [c] q3, q3 -> [d] q3"),
                        List.of("q4_* -> [c] q4, q4 - [d] q5, q5 -> [c] q6, q6 -> [d] q6, q6 - [c] q7, q7 -> [d] q4"));

        testReport.testFalse(testName,
                         (i1, i2) -> USER.isEquivalent(automaton(i1), automaton(i2)),
                             List.of("q1_* -> [c] q1, q1 - [d] q2, q2 - [c] q3, q3 -> [d] q3"),
                             List.of("q4_* -> [c] q4, q4 -> [d] q5, q5 -> [d] q6, q6 -> [d] q6, q6 - [c] q7, q7 -> [c] q4, q5 - [c] q7"));

        testReport.sectionDivider(testName);

        testReport.compareBoolean(testName,
                                  (i1, i2) -> USER.isEquivalent(automaton(i1), automaton(i2)),
                                  (i1, i2) -> SAMPLE.isEquivalent(automaton(i1), automaton(i2)),
                                  generateAutomatons(),
                                  generateAutomatons());
    }

    private void validateIsSimulatedBy() {
        String testName = "isSimulatedBy";

        testReport.testTrue(testName,
                (i1, i2) -> USER.isSimulatedBy(automaton(i1), automaton(i2)),
                List.of("s1_ -> [c] s3, s1 -> [c] s2, s3 -> [d] s5 , s2 -> [m] s4"),
                List.of("s6_ -> [c] s7, s7 -> [d] s9, s7 -> [m] s8"));

        testReport.testFalse(testName,
                (i1, i2) -> USER.isSimulatedBy(automaton(i1), automaton(i2)),
                List.of("s6_ -> [c] s7, s7 -> [d] s9, s7 -> [m] s8"),
                List.of("s1_ -> [c] s3, s1 -> [c] s2, s3 -> [d] s5 , s2 -> [m] s4"));

        testReport.sectionDivider(testName);

        testReport.compareBoolean(testName,
                                (i1, i2) -> USER.isSimulatedBy(automaton(i1), automaton(i2)),
                                (i1, i2) -> SAMPLE.isSimulatedBy(automaton(i1), automaton(i2)),
                                generateAutomatons(),
                                generateAutomatons());
    }

    private void validateAreReachable() {
        String testName = "areReachable";

        testReport.testTrue(testName,
                            automatonFunction.andThen(USER::areReachable),
                            List.of("s1_ -> [a] s2, s2 -> [b] s3, s3 -> [c] s4<",
                                    """
                                    S = {s0, s5<, s4, s8, s1, s7, s3, s6<, s2}
                                    I = {s0, s2}
                                    T = {(s0, s4) [c], (s0, s1) [b], (s5, s0) [b], (s4, s8) [c], (s4, s7) [c], (s8, s3) [b], (s1, s1) [a], (s7, s7) [a], (s7, s6) [c], (s3, s5) [b], (s3, s1) [a], (s6, s0) [a], (s2, s7) [a], (s2, s3) [a]}
                                    F = {s0, s3}"""));

        testReport.testFalse(testName,
                             automatonFunction.andThen(USER::areReachable),
                             List.of("s1_ -> [a] s2, s2 -> [b] s3, s4<",
                                     """
                                     S = {s0, s5<, s4, s8, s1, s7, s3, s6<, s2, s9, s10<}
                                     I = {s0, s2}
                                     T = {(s0, s4) [c], (s0, s1) [b], (s5, s0) [b], (s4, s8) [c], (s4, s7) [c], (s8, s3) [b], (s1, s1) [a], (s7, s7) [a], (s7, s6) [c], (s3, s5) [b], (s3, s1) [a], (s6, s0) [a], (s2, s7) [a], (s2, s3) [a], (s9, s10) [a], (s10, s9) [b]}
                                     F = {s0, s3}"""));

        testReport.sectionDivider(testName);

        testReport.compareBoolean(testName,
                                  automatonFunction.andThen(USER::areReachable),
                                  automatonFunction.andThen(SAMPLE::areReachable),
                                  generateAutomatons());
    }

    private void validateToProductAutomaton() {
        String testName = "toProductAutomaton";

        testReport.test(testName,
                        (i1, i2) -> USER.toProductAutomaton(automaton(i1), automaton(i2)),
                        List.of("s0_ -> [a] s1, s1 -> [b] s2*, s2 -> [a b] s2",
                                "s0_ -> [a] s1, s1 -> [b] s2*, s2 -> [a b] s2"),
                        List.of("t0_ -> [a b] t0, t0 -> [b] t1, t1 -> [a] t2*",
                                "t0_ -> [a] t1_"),
                        List.of(automaton("""
                                      S = {s1t0, s2t0, s2t2, s0t0, s2t1}
                                      I = {s0t0}
                                      T = {(s1t0, s2t0) [b], (s1t0, s2t1) [b], (s2t0, s2t0) [a b], (s2t0, s2t1) [b], (s0t0, s1t0) [a], (s2t1, s2t2) [a]}
                                      F = {s2t2}"""),
                                automaton("""
                                      S = {s0t0, s1t1, s0t1}
                                      I = {s0t0, s0t1}
                                      T = {(s0t0, s1t1) [a]}""")));

        testReport.sectionDivider(testName);

        testReport.compare(testName,
                           (i1, i2) -> USER.toProductAutomaton(automaton(i1), automaton(i2)),
                           (i1, i2) -> SAMPLE.toProductAutomaton(automaton(i1), automaton(i2)),
                           generateAutomatons(),
                           generateAutomatons());
    }

    private void validateToPowerAutomaton() {
        String testName = "toPowerAutomaton";

        testReport.test(testName,
                        automatonFunction.andThen(USER::toPowerAutomaton),
                        Map.of("s0_ -> [a b] s0, s0 -> [a] s1_*",
                               automaton("""
                                      S = {s0, s0s1}
                                      I = {s0s1}
                                      T = {(s0, s0) [b], (s0, s0s1) [a], (s0s1, s0) [b], (s0s1, s0s1) [a]}
                                      F = {s0s1}"""),
                               "s0_ -> [a] s1, s1 -> [b] s2, s1 -> [b] s3, s2 -> [a b] s2, s2 -> [b] s3, s3 -> [a] s4*",
                               automaton("""
                                      S = {s0, s1, s2, sink, s2s3, s2s4}
                                      I = {s0}
                                      T = {(s0, sink) [b], (s0, s1) [a], (s1, sink) [a], (s1, s2s3) [b], (s2, s2s3) [b],
                                      (s2, s2) [a], (sink, sink) [a b], (s2s3, s2s3) [b], (s2s3, s2s4) [a],
                                      (s2s4, s2s3) [b], (s2s4, s2) [a]}
                                      F = {s2s4}"""),
                               """
                                      s0_* -> [b0] s1_, s1 -> [a0 b0] s1, s0 -> [a0 b2] s3, s0 -> [b1] s2*,
                                      s2 -> [b0] s1, s3 -> [a0] s2, s2 -> [a0] s4*, s1 -> [b1] s4,
                                      s3 -> [b0] s4, s4 -> [a0 b0] s4""",
                               automaton("""
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
                                      F = {s1s4, s1s2, s2, s2s4, s4, s0s1}""")));

        testReport.sectionDivider(testName);

        testReport.compare(testName,
                           automatonFunction.andThen(USER::toPowerAutomaton),
                           automatonFunction.andThen(SAMPLE::toPowerAutomaton),
                           generateAutomatons());
    }

    private void validateToComplementAutomaton() {
        String testName = "toComplementAutomaton";

        testReport.test(testName,
                        automatonFunction.andThen(USER::toComplementAutomaton),
                        Map.of("s0_ -> [a] s1, s1 -> [a] s2*, s0 -> [a] s4*",
                               automaton("""
                                      S = {sink, s1s4, s0, s2}
                                      I = {s0}
                                      T = {(sink, sink) [a], (s1s4, s2) [a], (s0, s1s4) [a], (s2, sink) [a]}
                                      F = {sink, s0}
                                      """),
                               """
                                      s0_ -> [a] s1, s0 -> [b] sink, s1 -> [a] sink, sink -> [a b] sink,
                                      s1 -> [b] s2s3, s2s3 -> [b] s2s3, s2s3 -> [a] s2s4*, s2s4 -> [b] s2s3,
                                      s2s4 -> [a] s2, s2 -> [a] s2, s2 -> [b] s2s3""",
                               automaton("""
                                      S = {s2, sink, s0, s1, s2s4, s2s3}
                                      I = {s0}
                                      T = {(s2, s2s3) [b], (s2, s2) [a], (sink, sink) [a b], (s0, sink) [b], (s0, s1) [a], (s1, sink) [a], (s1, s2s3) [b], (s2s4, s2s3) [b], (s2s4, s2) [a], (s2s3, s2s4) [a], (s2s3, s2s3) [b]}
                                      F = {s2, sink, s0, s1, s2s3}""")));

        testReport.sectionDivider(testName);

        testReport.compare(testName,
                           automatonFunction.andThen(USER::toComplementAutomaton),
                           automatonFunction.andThen(SAMPLE::toComplementAutomaton),
                           generateAutomatons());
    }

    private void validateToSinkAutomaton() {
        String testName = "toSinkAutomaton";

        testReport.test(testName,
                        automatonFunction.andThen(USER::toSinkAutomaton),
                        Map.of("s1_ -> [a] s2",
                               automaton("""
                                      S = {s1, sink, s2}
                                      I = {s1}
                                      T = {(s1, s2) [a], (sink, sink) [a], (s2, sink) [a]}
                                      """),
                               "s1_ -> [a b] s2, s1 -> [a] s3, s2 -> [b] s3",
                               automaton("""
                                      S = {s3, s2, sink, s1}
                                      I = {s1}
                                      T = {(s3, sink) [a b], (s2, sink) [a], (s2, s3) [b], (sink, sink) [a b], (s1, s3) [a], (s1, s2) [a b]}""")));

        testReport.sectionDivider(testName);

        testReport.compare(testName,
                           automatonFunction.andThen(USER::toSinkAutomaton),
                           automatonFunction.andThen(SAMPLE::toSinkAutomaton),
                           generateAutomatons());
    }

    private void validateToOracleAutomaton() {
        String testName = "toOracleAutomaton";

        testReport.test(testName,
                        automatonFunction.andThen(USER::toOracleAutomaton),
                        Map.of("""
                                       s0_* -> [b] s1_, s1 -> [a b] s1, s0 -> [a b] s3, s0 -> [b] s2*, s2 -> [b] s1,
                                       s3 -> [a] s2, s2 -> [a] s4*, s1 -> [b] s4, s3 -> [b] s4, s4 -> [a b] s4""",
                               automaton("""
                                     S = {s0, s3, s2, s1, s4}
                                     I = {s0, s1}
                                     T = {(s0, s3) [as3 bs3], (s0, s1) [bs1], (s0, s2) [bs2], (s3, s4) [bs4], (s3, s2) [as2], (s2, s4) [as4], (s2, s1) [bs1], (s1, s4) [bs4], (s1, s1) [bs1 as1], (s4, s4) [bs4 as4]}
                                     F = {s0, s2, s4}""")));

        testReport.sectionDivider(testName);

        testReport.compare(testName,
                           automatonFunction.andThen(USER::toOracleAutomaton),
                           automatonFunction.andThen(SAMPLE::toOracleAutomaton),
                           generateAutomatons());
    }

    private void validateToOptimisedOracleAutomaton() {
        testReport.uncertain("toOptimisedOracleAutomaton",
                             "It's impossible to test this function due to its non-deterministic nature");
    }

    /* H E L P E R S */
    private FiniteAutomaton automaton(String s) {
        return Model.of(s).toFiniteAutomaton();
    }

    private List<String> generateAutomatons() {
        return IntStream.range(1, 23)
                .mapToObj(i -> FiniteAutomatonGenerator.generate(6,
                                                                 i % 3 + 1,
                                                                 i % 4 + 1,
                                                                 i % 3 + 1,
                                                                 i % 2,
                                                                 i % 4 + 1,
                                                                 i % 3 == 0))
                .map(fa -> fa.mapStates(state -> {
                    state.isEncodingEnd = Integer.parseInt(state.name.replace("s", "")) % 3 == 0;
                    return state;
                }))
                .map(FiniteAutomaton::toModel)
                .map(Model::toString)
                .toList();
    }

    private boolean isImplemented(Runnable r) {
        try {
            r.run();
            return true;
        } catch(Exception e) {
            return !(e instanceof IllegalStateException) && !e.getMessage().contains("not implemented yet!");
        }
    }
}

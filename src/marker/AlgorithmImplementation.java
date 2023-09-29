package marker;

import algorithmTester.SampleImplementation;
import algorithmTester.YourImplementation;
import model.finite.FiniteAutomaton;
import model.finite.State;
import model.kripke.KripkeStructure;

import java.util.*;
import java.util.stream.Collectors;

public interface AlgorithmImplementation {
    AlgorithmImplementation SAMPLE = new SampleImplementation();
    AlgorithmImplementation USER = new YourImplementation();

    boolean isDeterministic(FiniteAutomaton automaton);
    boolean isComplete(FiniteAutomaton automaton);
    boolean isEquivalent(FiniteAutomaton automaton1, FiniteAutomaton automaton2);
    FiniteAutomaton toProductAutomaton(FiniteAutomaton automaton1, FiniteAutomaton automaton2);
    FiniteAutomaton toPowerAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toComplementAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toSinkAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toOracleAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toOptimisedOracleAutomaton(FiniteAutomaton automaton);

    /* H E L P E R S */
    default String combinedName(State... nodes) {
        return combinedName(Arrays.stream(nodes).toList());
    }

    default String combinedName(Collection<State> collection) {
        return collection.isEmpty()
                ? "sink"
                : collection.stream()
                .map(node -> node.name)
                .sorted()
                .collect(Collectors.joining());
    }
}

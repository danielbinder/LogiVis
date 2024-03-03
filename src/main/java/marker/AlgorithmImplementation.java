package marker;

import algorithmTester.YourImplementation;
import model.variant.finite.FiniteAutomaton;
import model.variant.finite.State;
import model.variant.finite.interpreter.SampleImplementation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public interface AlgorithmImplementation {
    AlgorithmImplementation SAMPLE = new SampleImplementation();
    AlgorithmImplementation USER = new YourImplementation();

    boolean isDeterministic(FiniteAutomaton automaton);
    boolean isComplete(FiniteAutomaton automaton);
    boolean isEquivalent(FiniteAutomaton automaton1, FiniteAutomaton automaton2);
    boolean isSimulatedBy(FiniteAutomaton automaton1, FiniteAutomaton automaton2);
    boolean areReachable(FiniteAutomaton automaton);
    FiniteAutomaton toProductAutomaton(FiniteAutomaton automaton1, FiniteAutomaton automaton2);
    FiniteAutomaton toPowerAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toComplementAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toSinkAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toOracleAutomaton(FiniteAutomaton automaton);
    FiniteAutomaton toOptimisedOracleAutomaton(FiniteAutomaton automaton);
    Set<Set<State>> getStronglyConnectedComponents(FiniteAutomaton automaton);

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

    default String unsortedCombinedName(State... nodes) {
        return unsortedCombinedName(Arrays.stream(nodes).toList());
    }

    default String unsortedCombinedName(Collection<State> collection) {
        return collection.isEmpty()
                ? "sink"
                : collection.stream()
                .map(node -> node.name)
                .collect(Collectors.joining());
    }
}

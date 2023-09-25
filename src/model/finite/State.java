package model.finite;

import java.util.*;
import java.util.stream.Collectors;

public class State {
    public String name = "";
    public boolean isInitialState = false;
    public boolean isFinalState = false;
    public boolean isEncodingStart = false;
    public boolean isEncodingEnd = false;
    /** Map<Successor, TransitionLabel> */
    private final Map<String, Set<State>> successors = new HashMap<>();

    public boolean hasSuccessor(State node) {
        return successors.values().stream()
                .anyMatch(succSet -> succSet.contains(node));
    }

    public boolean hasProperty(String property) {
        return successors.containsKey(property);
    }

    public Set<State> getSuccessors() {
        return successors.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<String> getSuccessorProperties() {
        return successors.keySet();
    }

    public Set<State> getSuccessorsFor(String property) {
        return successors.containsKey(property)
                ? Collections.unmodifiableSet(successors.get(property))
                : Collections.emptySet();
    }

    public State addSuccessor(String property, State successor) {
        if(!successors.containsKey(property)) successors.put(property, new HashSet<>());

        successors.get(property).add(successor);

        return this;
    }

    @SafeVarargs
    public final State addSuccessors(String property, Set<State>... successorSets) {
        Arrays.stream(successorSets).forEach(set -> set.forEach(succ -> addSuccessor(property, succ)));

        return this;
    }

    @Override
    public String toString() {
        return name +
                (isInitialState ? "_" : "") +
                (isFinalState ? "*" : "") +
                (isEncodingStart ? ">" : "") +
                (isEncodingEnd ? "<" : "") +
                (!successors.isEmpty() ? " -> " : "") +
                getSuccessors().stream()
                        .map(succ -> succ.name)
                        .collect(Collectors.joining(", "));
    }
}

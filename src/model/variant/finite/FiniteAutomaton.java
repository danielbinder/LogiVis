package model.variant.finite;

import model.parser.Model;
import model.parser.ModelNode;
import model.variant.ModelVariant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static marker.AlgorithmImplementation.SAMPLE;

public class FiniteAutomaton extends HashSet<State> implements ModelVariant {
    public static FiniteAutomaton of(Supplier<Collection<State>> stateSupplier) {
        FiniteAutomaton automaton = new FiniteAutomaton();
        automaton.addAll(stateSupplier.get());

        return automaton;
    }

    public static FiniteAutomaton of(Collection<State> states) {
        FiniteAutomaton automaton = new FiniteAutomaton();
        automaton.addAll(states);

        return automaton;
    }

    public State get(String name) {
        return stream()
                .filter(state -> state.name.equals(name))
                .findAny()
                .orElse(null);      // in a good implementation, this should never happen
    }

    /**
     * Gets a state by name or creates (and adds) one with that name if it doesn't exist
     * @return State
     */
    public State getOrCreate(String name) {
        State state = get(name);

        if(state == null) {
            state = new State();
            state.name = name;

            add(state);
        }

        return state;
    }

    public Set<State> getInitialStates() {
        return filter(state -> state.isInitialState);
    }

    public Set<State> getFinalStates() {
        return filter(state -> state.isFinalState);
    }

    public Set<State> getEncodingStarts() {
        return filter(state -> state.isEncodingStart);
    }

    public Set<State> getEncodingEnds() {
        return filter(state -> state.isEncodingEnd);
    }

    public FiniteAutomaton filter(Predicate<State> filter) {
        return stream()
                .filter(filter)
                .collect(collector());
    }

    public boolean anyMatch(Predicate<State> predicate) {
        return stream().anyMatch(predicate);
    }

    public boolean allMatch(Predicate<State> predicate) {
        return stream().allMatch(predicate);
    }

    public boolean noneMatch(Predicate<State> predicate) {
        return stream().noneMatch(predicate);
    }

    public Set<String> getAlphabet() {
        return stream()
                .flatMap(state -> state.getSuccessorProperties().stream())
                .collect(Collectors.toSet());
    }

    public boolean contains(String name) {
        return stream().anyMatch(state -> state.name.equals(name));
    }

    public <T> Set<T> map(Function<State, T> stateMapper) {
        return stream()
                .map(stateMapper)
                .collect(Collectors.toSet());
    }

    public FiniteAutomaton mapStates(Function<State, State> stateMapper) {
        return stream()
                .map(stateMapper)
                .collect(collector());
    }

    public <T> Set<T> flatMap(Function<State, Stream<T>> stateMapper) {
        return stream()
                .flatMap(stateMapper)
                .collect(Collectors.toSet());
    }

    public FiniteAutomaton flatMapStates(Function<State, Stream<State>> stateMapper) {
        return stream()
                .flatMap(stateMapper)
                .collect(collector());
    }

    public static Collector<State, Set<State>, FiniteAutomaton> collector() {
        return new Collector<>() {
            @Override
            public Supplier<Set<State>> supplier() {
                return HashSet::new;
            }

            @Override
            public BiConsumer<Set<State>, State> accumulator() {
                return Set::add;
            }

            @Override
            public BinaryOperator<Set<State>> combiner() {
                return (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                };
            }

            @Override
            public Function<Set<State>, FiniteAutomaton> finisher() {
                return FiniteAutomaton::of;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    public Model toModel() {
        Model model = new Model();
        forEach(bn -> {
            ModelNode mn = new ModelNode(bn.name);
            mn.isInitialNode = bn.isInitialState;
            mn.isFinalNode = bn.isFinalState;
            mn.isEncodingStartPoint = bn.isEncodingStart;
            mn.isEncodingEndPoint = bn.isEncodingEnd;

            model.add(mn);
        });

        forEach(bn -> bn.getSuccessorProperties()
                .forEach(prop -> bn.getSuccessorsFor(prop)
                        .forEach(succ -> {
                            ModelNode origin = model.get(bn.name);
                            ModelNode successor = model.get(succ.name);
                            if(origin.successors.containsKey(successor))
                                origin.successors.put(successor, origin.successors.get(successor) + " " + prop);
                            else origin.successors.put(successor, prop);
                        })));

        return model;
    }

    @Override
    public FiniteAutomaton clone() {
        FiniteAutomaton clone = new FiniteAutomaton();

        forEach(state -> {
            State s = clone.getOrCreate(state.name);
            s.isInitialState = state.isInitialState;
            s.isFinalState = state.isFinalState;
            s.isEncodingStart = state.isEncodingStart;
            s.isEncodingEnd = state.isEncodingEnd;

            state.getSuccessorProperties()
                    .forEach(prop -> state.getSuccessorsFor(prop)
                            .forEach(succ -> s.addSuccessor(prop, clone.getOrCreate(succ.name))));
        });

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FiniteAutomaton fa) {
            return fa.stream()
                    .allMatch(state -> contains(state.name) &&
                            get(state.name).isInitialState == fa.get(state.name).isInitialState &&
                            get(state.name).isFinalState == fa.get(state.name).isFinalState &&
                            get(state.name).isEncodingStart == fa.get(state.name).isEncodingStart &&
                            get(state.name).isEncodingEnd == fa.get(state.name).isEncodingEnd &&
                            get(state.name).getSuccessorProperties().stream()
                                    .allMatch(prop -> get(state.name).getSuccessorsFor(prop).stream()
                                            .map(succ -> succ.name)
                                            .collect(Collectors.toSet())
                                            .equals(fa.get(state.name).getSuccessorsFor(prop).stream()
                                                            .map(succ -> succ.name)
                                                            .collect(Collectors.toSet()))));
        } else return false;
    }

    public boolean isDeterministic() {
        return SAMPLE.isDeterministic(this);
    }

    public boolean isComplete() {
        return SAMPLE.isComplete(this);
    }

    public boolean isEquivalent(FiniteAutomaton other) {
        return SAMPLE.isEquivalent(this, other);
    }

    public FiniteAutomaton toProductAutomaton(FiniteAutomaton other) {
        return SAMPLE.toProductAutomaton(this, other);
    }

    public FiniteAutomaton toPowerAutomaton() {
        return SAMPLE.toPowerAutomaton(this);
    }

    public FiniteAutomaton toComplementAutomaton() {
        return SAMPLE.toComplementAutomaton(this);
    }

    public FiniteAutomaton toSinkAutomaton() {
        return SAMPLE.toSinkAutomaton(this);
    }

    public FiniteAutomaton toOracleAutomaton() {
        return SAMPLE.toOracleAutomaton(this);
    }

    public FiniteAutomaton toOptimisedOracleAutomaton() {
        return SAMPLE.toOptimisedOracleAutomaton(this);
    }

    @Override
    public String toString() {
        return toModel().toString();
    }
}

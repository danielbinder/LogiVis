package model.finite;

import marker.AlgorithmImplementation;
import marker.ModelVariant;
import model.parser.Model;
import model.parser.ModelNode;
import util.Pair;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public boolean isEquivalentTo(FiniteAutomaton other) {
        FiniteAutomaton current = this;
        if(!isDeterministic()) current = toPowerAutomaton();
        else if(!current.isComplete()) current = toSinkAutomaton();
        if(!other.isDeterministic()) other = other.toPowerAutomaton();
        else if(!other.isComplete()) other = other.toSinkAutomaton();

        if(!current.getAlphabet().equals(other.getAlphabet())) return false;

        Pair<State, State> initialPair = Pair.of(current.getInitialStates().stream()
                                                       .findAny()
                                                       .orElseThrow(NoSuchElementException::new),
                                               other.getInitialStates().stream()
                                                       .findAny()
                                                       .orElseThrow(NoSuchElementException::new));

        return evaluatePair(new HashSet<>(), initialPair, current.getAlphabet());
    }

    private boolean evaluatePair(Set<Pair<State, State>> visited, Pair<State, State> pair, Set<String> alphabet) {
        if(visited.contains(pair)) return true;
        visited.add(pair);

        return pair.left.isInitialState == pair.right.isInitialState &&
                pair.left.isFinalState == pair.right.isFinalState &&
                alphabet.stream()
                        .allMatch(letter -> evaluatePair(visited,
                                                         Pair.of(pair.left.getSuccessorsFor(letter).stream()
                                                                         .findAny()
                                                                         .orElseThrow(NoSuchElementException::new),
                                                                 pair.right.getSuccessorsFor(letter).stream()
                                                                         .findAny()
                                                                         .orElseThrow(NoSuchElementException::new)),
                                                         alphabet));
    }

    public boolean isDeterministic() {
        return AlgorithmImplementation.SAMPLE.isDeterministic(this);
    }

    public boolean isComplete() {
        return AlgorithmImplementation.SAMPLE.isComplete(this);
    }

    public FiniteAutomaton toProductAutomaton(FiniteAutomaton other) {
        return AlgorithmImplementation.SAMPLE.toProductAutomaton(this, other);
    }

    public FiniteAutomaton toPowerAutomaton() {
        return AlgorithmImplementation.SAMPLE.toPowerAutomaton(this);
    }

    public FiniteAutomaton toComplementAutomaton() {
        return AlgorithmImplementation.SAMPLE.toComplementAutomaton(this);
    }

    public FiniteAutomaton toSinkAutomaton() {
        return AlgorithmImplementation.SAMPLE.toSinkAutomaton(this);
    }

    public FiniteAutomaton toOracleAutomaton() {
        return AlgorithmImplementation.SAMPLE.toOracleAutomaton(this);
    }

    public FiniteAutomaton toOptimisedOracleAutomaton() {
        return AlgorithmImplementation.SAMPLE.toOptimisedOracleAutomaton(this);
    }
}

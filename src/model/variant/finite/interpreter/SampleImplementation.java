package model.variant.finite.interpreter;

import marker.AlgorithmImplementation;
import model.variant.finite.FiniteAutomaton;
import model.variant.finite.State;
import util.Logger;
import util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SampleImplementation implements AlgorithmImplementation {
    private static final List<String> solutionInformation = new ArrayList<>();
    public static List<String> getSolutionInformation() {
        List<String> result = new ArrayList<>(solutionInformation);
        solutionInformation.clear();
        return result;
    }

    @Override
    public boolean isDeterministic(FiniteAutomaton automaton) {
        // Example of a solution without streams ('CTRL + /' to comment in/out highlighted lines)
//        if(automaton.getInitialStates().size() > 1) return false;
//
//        for(State state : automaton) {
//            for(String property : state.getSuccessorProperties()) {
//                if(state.getSuccessorsFor(property).size() > 1) return false;
//            }
//        }
//
//        return true;
        solutionInformation.add("Checking for determinism");
        solutionInformation.add("Amount of initial states: " + automaton.getInitialStates().size());
        if(automaton.getInitialStates().size() <= 1)
            solutionInformation.add("Going through all states and checking their successors for the respective properties");
        else solutionInformation.add("This automaton is therefore not deterministic");
        return automaton.getInitialStates().size() <= 1 && automaton
                .allMatch(state -> state.getSuccessorProperties().stream()
                        .allMatch(prop -> {
                            Set<State> succ = state.getSuccessorsFor(prop);
                            solutionInformation.add(state.name + " has " + succ.size() + " successors with property " + prop + ": " +
                                                            succ.stream()
                                                                    .map(s -> s.name)
                                                                    .collect(Collectors.joining(", ")));

                            if(succ.size() <= 1) return true;

                            solutionInformation.add("This automaton is therefore not deterministic");
                            return false;
                        }));
    }

    @Override
    public boolean isComplete(FiniteAutomaton automaton) {
        solutionInformation.add("Checking for completeness");
        int alphabetSize = automaton.getAlphabet().size();
        solutionInformation.add("The alphabet size is " + alphabetSize);

        return automaton.allMatch(state -> {
            Set<String> successorProperties = state.getSuccessorProperties();
            solutionInformation.add(state.name + " has " + successorProperties.size() + " successor properties : " +
                                            String.join(", ", successorProperties));

            if(successorProperties.size() == alphabetSize) return true;

            solutionInformation.add("This automaton is therefore not complete");
            return false;
        });
    }

    @Override
    public boolean isEquivalent(FiniteAutomaton automaton1, FiniteAutomaton automaton2) {
        solutionInformation.add("Checking if both automatons are equivalent");
        solutionInformation.add("Checking if both automatons are equal, since they would also be equivalent");
        if(automaton1.equals(automaton2)) return true;

        automaton1 = automaton1.clone();
        automaton2 = automaton2.clone();

        if(automaton1.getInitialStates().isEmpty())
            throw new IllegalArgumentException("This algorithm needs initial states to work! The first automaton does not have any!");
        if(automaton2.getInitialStates().isEmpty())
            throw new IllegalArgumentException("This algorithm needs initial states to work! The second automaton does not have any!");

        if(!automaton1.isDeterministic()) automaton1 = automaton1.toPowerAutomaton();
        else if(!automaton1.isComplete()) automaton1 = automaton1.toSinkAutomaton();
        if(!automaton2.isDeterministic()) automaton2 = automaton2.toPowerAutomaton();
        else if(!automaton2.isComplete()) automaton2 = automaton2.toSinkAutomaton();
        solutionInformation.add("Using the following deterministic and complete versions of automatons:");
        solutionInformation.add(automaton1.toModel().toString());
        solutionInformation.add(automaton2.toModel().toString());

        if(!automaton1.getAlphabet().equals(automaton2.getAlphabet())) {
            solutionInformation.add("Both automatons have different alphabets:\n" +
                                            automaton1.getAlphabet() + "\n" +
                                            automaton2.getAlphabet());
            solutionInformation.add("They are therefore not equal");
            return false;
        }

        solutionInformation.add("Evaluating all pairs starting with initial states");
        Pair<State, State> initialPair = Pair.of(automaton1.getInitialStates().stream()
                                                         .findAny()
                                                         .orElseThrow(NoSuchElementException::new),
                                                 automaton2.getInitialStates().stream()
                                                         .findAny()
                                                         .orElseThrow(NoSuchElementException::new));

        return evaluatePair(new HashSet<>(), initialPair, automaton1.getAlphabet());
    }

    private boolean evaluatePair(Set<Pair<State, State>> visited, Pair<State, State> pair, Set<String> alphabet) {
        solutionInformation.add("Evaluating pair (" + pair.left.name + ", " + pair.right.name + ")");
        if(visited.contains(pair)) {
            solutionInformation.add("Pair was already visited before");
            return true;
        }
        visited.add(pair);

        boolean result = pair.left.isInitialState == pair.right.isInitialState &&
                pair.left.isFinalState == pair.right.isFinalState &&
                alphabet.stream()
                        .allMatch(letter -> {
                            solutionInformation.add("Evaluating '" + letter + "' successor for ("
                                                            + pair.left.name + ", " + pair.right.name + ")");

                            return evaluatePair(visited,
                                         Pair.of(pair.left.getSuccessorsFor(letter).stream()
                                                         .findAny()
                                                         .orElseThrow(NoSuchElementException::new),
                                                 pair.right.getSuccessorsFor(letter).stream()
                                                         .findAny()
                                                         .orElseThrow(NoSuchElementException::new)),
                                         alphabet);
                        });

        if(pair.left.isInitialState != pair.right.isInitialState)
            solutionInformation.add("One state of the pair is an initial state, while the other one is not\n" +
                                            "The automatons are therefore not equal");
        else if(pair.left.isFinalState == pair.right.isFinalState)
            solutionInformation.add("One state of the pair is a final state, while the other one is not\n" +
                                            "The automatons are therefore not equal");

        return result;
    }

    @Override
    public boolean areReachable(FiniteAutomaton automaton) {
        Set<State> toCheck = automaton.getEncodingEnds();
        solutionInformation.add("Checking if the following states are reachable: " + toCheck.stream()
                .map(state -> state.name)
                .collect(Collectors.joining(", ")));

        if(automaton.getInitialStates().isEmpty()) {
            throw new IllegalArgumentException("This algorithm needs initial states to work! The first automaton does not have any!");
        }
        if(toCheck.isEmpty()) {
            throw new IllegalArgumentException("This algorithm needs some states to be marked with '<', meaning those states need to be checked!");
        }

        Set<State> visited = new HashSet<>();
        automaton.getInitialStates().forEach(initial -> {
            solutionInformation.add("Starting from " + initial.name + " checking reachability of states " + toCheck.stream()
                    .map(state -> state.name)
                    .collect(Collectors.joining(", ")));
            walk(visited, initial);
        });

        solutionInformation.add("The visited states are " +
                                        visited.stream()
                                                .map(state -> state.name)
                                                .collect(Collectors.joining(", ")) +
                                        " and the states to check are " +
                                        toCheck.stream()
                                                .map(state -> state.name)
                                                .collect(Collectors.joining(", ")));
        return visited.containsAll(toCheck);
    }

    private void walk(Set<State> visited, State current) {
        if(visited.contains(current)) {
            solutionInformation.add(current.name + " already in visited states");
            return;
        }

        solutionInformation.add("Adding " + current.name + " to visited states");
        visited.add(current);
        solutionInformation.add("Visiting all successors of " + current.name + ":" + current.getSuccessors().stream()
                .map(state -> state.name)
                .collect(Collectors.joining(", ")));
        current.getSuccessors().forEach(succ -> walk(visited, succ));
    }

    @Override
    public FiniteAutomaton toProductAutomaton(FiniteAutomaton automaton1, FiniteAutomaton automaton2) {
        solutionInformation.add("Creating product automaton");
        if(automaton1.getInitialStates().isEmpty()) {
            throw new IllegalArgumentException("This algorithm needs initial states to work! The first automaton does not have any!");
        }
        if(automaton2.getInitialStates().isEmpty()) {
            throw new IllegalArgumentException("This algorithm needs initial states to work! The second automaton does not have any!");
        }
        automaton1 = automaton1.clone();
        automaton2 = automaton2.clone();

        FiniteAutomaton power = new FiniteAutomaton();

        FiniteAutomaton finalAutomaton = automaton2;
        solutionInformation.add("Constructing automaton on the fly, starting with initial states of both automatons:");
        solutionInformation.add(automaton1.getInitialStates().stream().map(state -> state.name).collect(Collectors.joining(", ")));
        solutionInformation.add(automaton2.getInitialStates().stream().map(state -> state.name).collect(Collectors.joining(", ")));
        automaton1.getInitialStates()
                 .forEach(s1 -> finalAutomaton.getInitialStates()
                         .forEach(s2 -> recursiveProductAutomaton(s1, s2, power)));

         return power;
    }

    private void recursiveProductAutomaton(State s1, State s2, FiniteAutomaton product) {
        // S = S1 x S2
        State productstate = product.getOrCreate(unsortedCombinedName(s1, s2));
        solutionInformation.add("Creating product state " + productstate.name + " for " + s1.name + " and " + s2.name);
        // I = I1 x I2
        if(s1.isInitialState && s2.isInitialState) {
            solutionInformation.add(productstate.name + " is an initial state, since both " + s1.name + " and " + s2.name + " are initial states");
            productstate.isInitialState = true;
        }
        // F = F1 x F2
        if(s1.isFinalState && s2.isFinalState) {
            solutionInformation.add(productstate.name + " is a final state, since both " + s1.name + " and " + s2.name + " are final states");
            productstate.isFinalState = true;
        }

        product.add(productstate);

        for(String prop1 : s1.getSuccessorProperties()){
            for(State succ1 : s1.getSuccessorsFor(prop1)) {
                for(State succ2 : s2.getSuccessorsFor(prop1)) {
                    if(productstate.getSuccessorsFor(prop1).contains(product.getOrCreate(unsortedCombinedName(succ1, succ2)))) continue;

                    solutionInformation.add("Both " + s1.name + " and " +
                                            s2.name + " transition with " + prop1 + " to " + succ1.name + " and " +
                                                    succ2.name + " respectively");
                    solutionInformation.add("Therefore, for state " + productstate.name + " adding transition with '" +
                                                    prop1 + "' to " + unsortedCombinedName(succ1, succ2));
                    productstate.addSuccessor(prop1, product.getOrCreate(unsortedCombinedName(succ1, succ2)));

                    recursiveProductAutomaton(succ1, succ2, product);
                }
            }
        }

    }

    @Override
    public FiniteAutomaton toPowerAutomaton(FiniteAutomaton automaton) {
        solutionInformation.add("Creating power automaton");
        if(automaton.isDeterministic() && automaton.isComplete()) {
            solutionInformation.add("Automaton is already deterministic and complete," +
                                            " hence on the fly construction would lead to the same automaton");
            return automaton;
        }
        automaton = automaton.clone();

        if(automaton.getInitialStates().isEmpty())
            throw new IllegalArgumentException("This algorithm needs initial states to work, but this automaton does not have any!");
        solutionInformation.add("Constructing power automaton on the fly starting with initial States " +
                                        automaton.getInitialStates().stream()
                                                .map(state -> state.name)
                                                .collect(Collectors.joining(", ")));
        FiniteAutomaton power = recursivePowerAutomaton(automaton, new FiniteAutomaton(), automaton.getInitialStates());

        if(power.contains("sink")) {
            solutionInformation.add("Adding all transitions from 'sink' to 'sink'");
            State sink = power.get("sink");
            automaton.getAlphabet()
                    .forEach(letter -> sink.addSuccessor(letter, sink));
        }

        return power;
    }

    private FiniteAutomaton recursivePowerAutomaton(FiniteAutomaton original, FiniteAutomaton power, Set<State> current) {
        State powerstate = power.getOrCreate(combinedName(current));
        solutionInformation.add("Creating power automaton state " + powerstate.name + " from states " +
                                        current.stream()
                                                .map(state -> state.name)
                                                .collect(Collectors.joining(", ")));

        if(powerstate.name.equals(combinedName(original.getInitialStates()))) {
            solutionInformation.add(powerstate.name + " is an initial State, since all components are initial states");
            powerstate.isInitialState = true;
        }
        powerstate.isFinalState = original.filter(current::contains).anyMatch(state -> {
            if(state.isFinalState) {
                solutionInformation.add(powerstate.name + " is a final State, since at least one component '" +
                                                state.name + "' is a final state");
                return true;
            }

            return false;
        });

        for(String prop : original.getAlphabet()) {
            Set<State> successors = original
                    .filter(current::contains)
                    .flatMap(state -> state.getSuccessorsFor(prop).stream());
            solutionInformation.add("For states " + current.stream()
                                            .map(state -> state.name)
                                            .collect(Collectors.joining(", ")) +
                                            " grouping all '" + prop + "' successors of the original automaton: " +
                                            successors.stream()
                                                    .map(state -> state.name)
                                                    .collect(Collectors.joining(", ")));

            if(successors.isEmpty()) {
                solutionInformation.add("Adding transition to sink state, since there are no '" + prop + "' successors");
                powerstate.addSuccessor(prop, power.getOrCreate("sink"));
            }
            else if(powerstate.getSuccessorsFor(prop).contains(power.getOrCreate(combinedName(successors)))) {
                solutionInformation.add("Transition from " + powerstate.name + " to " + combinedName(successors) +
                                                " already exists");
                continue;
            }
            else {
                // link to successors
                solutionInformation.add("Linking " + powerstate.name + " to successor " + combinedName(successors));
                powerstate.addSuccessor(prop, power.get(combinedName(successors)));

                // create full successors
                recursivePowerAutomaton(original, power, successors);
            }
        }

        return power;
    }


    @Override
    public boolean isSimulatedBy(FiniteAutomaton automaton1, FiniteAutomaton automaton2) {
        if(automaton1.equals(automaton2)) return true;

        automaton1 = automaton1.clone(); // automaton A
        automaton2 = automaton2.clone(); // automaton B

        if(automaton1.getInitialStates().isEmpty())
            throw new IllegalArgumentException("This algorithm needs initial states to work! The first automaton does not have any!");
        if(automaton2.getInitialStates().isEmpty())
            throw new IllegalArgumentException("This algorithm needs initial states to work! The second automaton does not have any!");

        // initial relation (all state relations are true)
        Map<State, Map<State, Boolean>> currSim = new LinkedHashMap<>();
        for(State s : automaton1)
            currSim.put(s, automaton2.stream().collect(Collectors.toMap(Function.identity(), state -> Boolean.TRUE)));
        Map<State, Map<State, Boolean>> oldSim = deepCopyOfRelation(currSim);

        printSimulationRelation(currSim);

        // if a state of automaton B has no successors, a state from automaton A cannot be simulated
        // only exception: if the state from automaton A also lacks any successors, the state may be simulated
        // (i.e., empty set comparison)
        final List<State> noSuccessors = new ArrayList<>(automaton2.stream()
                .filter(state -> state.getSuccessors().isEmpty()).toList());
        currSim.forEach((aState, value) -> {
            if (!aState.getSuccessors().isEmpty()) {
                noSuccessors.forEach(noSuccState -> value.replace(noSuccState, Boolean.FALSE));
            }
        });

        printSimulationRelation(currSim);

        // as soon as oldSim and currSim are equal, the simulation relation has stabilized
        // and will not change anymore
        boolean initialRun = true;
        while(initialRun || !currSim.equals(oldSim)) {
            if(!initialRun) oldSim = deepCopyOfRelation(currSim);
            else initialRun = false;

            for(State aState : automaton1) {
                // if a state of automaton A does have successors, the relation must be investigated further
                // (otherwise, the relation will always remain TRUE)
                if(!aState.getSuccessors().isEmpty()) {
                    final Set<String> aStateTransitionLabels = aState.getSuccessorProperties();
                    for(State bState : automaton2) {
                        // if a state of automaton B does have successors, the relation must be investigated further
                        // (otherwise, the relation will always remain FALSE)
                        if(!bState.getSuccessors().isEmpty()) {
                            final Set<String> bStateTransitionLabels = bState.getSuccessorProperties();
                            // evaluate whether the current state from automaton B simulates the current state
                            // from automaton A
                            if(bStateTransitionLabels.containsAll(aStateTransitionLabels)
                                && oldSim.get(aState).get(bState)) {
                                boolean stateSimulated = true;
                                simulationLoop:
                                for(String transitionLabel : aStateTransitionLabels) {
                                    for(State aSucc : aState.getSuccessorsFor(transitionLabel)) {
                                        boolean successorSimulated = false;
                                        for(State bSucc : bState.getSuccessorsFor(transitionLabel)) {
                                            if(oldSim.get(aSucc).get(bSucc)) {
                                                successorSimulated = true;
                                                break;
                                            }
                                        }
                                        if(!successorSimulated) {
                                            stateSimulated = false;
                                            break simulationLoop;
                                        }
                                    }
                                }
                                currSim.get(aState).put(bState, stateSimulated);
                            } else {
                                // if the "transition alphabet" of the state from automaton B does not cover
                                // the entire "transition alphabet" of the state from automaton A, a simulation
                                // is not possible
                                currSim.get(aState).put(bState, Boolean.FALSE);
                            }
                        }
                    }
                }
            }
            printSimulationRelation(currSim);
        }

        // check if all initial states of automaton A are simulated by automaton B
        final Set<State> bInitial = automaton2.getInitialStates();
        return automaton1.getInitialStates().stream().map(aInit -> bInitial.stream()
                .map(bInit -> currSim.get(aInit).get(bInit)).reduce(true, (initial, val) -> initial && val))
                .reduce(true, (initial, val) -> initial && val);
    }

    private void printSimulationRelation(final Map<State, Map<State, Boolean>> relation) {
        final StringBuilder sb = new StringBuilder("\n");
        final Set<State> bStates = relation.values().stream().findFirst().get().keySet();
        sb.append("\t").append(bStates.stream().map(state -> state.name).collect(Collectors.joining(" ")))
                .append("\n");
        for(var entry : relation.entrySet()) {
            sb.append(String.format("%s  ", entry.getKey().name));
            sb.append(entry.getValue().values().stream().map(bool -> bool ? "1" : "0")
                    .collect(Collectors.joining("  "))).append("\n");
        }
        Logger.info(sb.toString());
    }

    private Map<State, Map<State, Boolean>> deepCopyOfRelation(final Map<State, Map<State, Boolean>> original) {
        return original.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                ))
                ));
    }

    @Override
    public FiniteAutomaton toComplementAutomaton(FiniteAutomaton automaton) {
        solutionInformation.add("Creating complement automaton");
        if(!automaton.isDeterministic()) automaton = toPowerAutomaton(automaton);
        else if(!automaton.isComplete()) automaton = toSinkAutomaton(automaton);

        solutionInformation.add("Making final states non final and vice versa");
        automaton.forEach(state -> {
            solutionInformation.add("Making " + state.name + (state.isFinalState ? "non" : "") + " final");
            state.isFinalState = !state.isFinalState;
        });

        return automaton;
    }

    @Override
    public FiniteAutomaton toSinkAutomaton(FiniteAutomaton automaton) {
        solutionInformation.add("Creating sink automaton");
        if(automaton.isComplete()) {
            solutionInformation.add("Automaton is already complete");
            return automaton;
        }
        automaton = automaton.clone();

        solutionInformation.add("Creating 'sink' state");
        FiniteAutomaton sink = new FiniteAutomaton();
        State sinkstate = sink.getOrCreate("sink");
        // Add all successors to self
        Set<String> alphabet = automaton.getAlphabet();
        solutionInformation.add("Adding all possible successors of 'sink' to 'sink'");
        alphabet.forEach(input -> sinkstate.addSuccessor(input, sinkstate));

        // Add all states from 'automaton' as they are
        solutionInformation.add("Adding all existing states to sink automaton");
        sink.addAll(automaton);

        for(State state : automaton)
            for(String input : alphabet)
                if(!state.getSuccessorProperties().contains(input)) {
                    solutionInformation.add("Adding transition from " + state.name + " to 'sink' with property " + input);
                    state.addSuccessor(input, sinkstate);
                }

        return sink;
    }

    @Override
    public FiniteAutomaton toOracleAutomaton(FiniteAutomaton automaton) {
        solutionInformation.add("Creating oracle automaton");
        FiniteAutomaton oracle = new FiniteAutomaton();

        for(State state : automaton) {
            State oraclestate = new State();
            oraclestate.name = state.name;
            oraclestate.isInitialState = state.isInitialState;
            oraclestate.isFinalState = state.isFinalState;

            for(String property : state.getSuccessorProperties()) {
                for(State successor : state.getSuccessorsFor(property)) {
                    oraclestate.addSuccessor(property + successor.name, successor);
                    solutionInformation.add("Converting " + state.name + " -> [" + property + "] " + successor.name + " to " +
                            oraclestate.name + " -> [" + property + successor.name + "] " + successor.name);
                }
            }

            oracle.add(oraclestate);
        }

        return oracle;
    }

    @Override
    public FiniteAutomaton toOptimisedOracleAutomaton(FiniteAutomaton automaton) {
        solutionInformation.add("Creating optimised oracle automaton");
        FiniteAutomaton optimisedOracle = new FiniteAutomaton();

        for(State state : automaton) {
            State optimisedOraclestate = new State();
            optimisedOraclestate.name = state.name;
            optimisedOraclestate.isInitialState = state.isInitialState;
            optimisedOraclestate.isFinalState = state.isFinalState;

            for(String prop : state.getSuccessorProperties()) {
                int i = 0;
                for(State successor : state.getSuccessorsFor(prop)) {
                    optimisedOraclestate.addSuccessor(prop + i, successor);
                    solutionInformation.add("Converting " + state.name + " -> [" + prop + "] " + successor.name + " to " +
                                                    optimisedOraclestate.name + " -> [" + prop + i + "] " + successor.name);
                    i++;
                }
            }

            optimisedOracle.add(optimisedOraclestate);
        }

        return optimisedOracle;
    }

    @Override
    public Set<Set<State>> getStronglyConnectedComponents(FiniteAutomaton automaton) {
        solutionInformation.add("Calculating strongly connected components");
        return filterDuplicates(new HashSet<>(automaton).stream()
                .map(state -> {
                    solutionInformation.add("Calculating strongly connected components for state " + state.name);
                    return getStronglyConnectedComponents(state, state, new HashSet<>());
                })
                .filter(set -> !set.isEmpty())
                .collect(Collectors.toUnmodifiableSet()));
    }

    private Set<State> getStronglyConnectedComponents(State start, State current, Set<State> visited) {
        if(current.equals(start) && !visited.isEmpty()) {
            solutionInformation.add("Found connection back to " + start.name +
                                            ", hence path to here contains strongly connected components");
            return new HashSet<>(Set.of(current));
        }
        if(visited.contains(current)) {
            solutionInformation.add("State " + current.name + " was already visited before");
            return new HashSet<>();
        }
        solutionInformation.add("Adding " + current.name + " to visited states");
        visited.add(current);

        solutionInformation.add("Visiting all successors of " + current.name + ": " + current.getSuccessors().stream()
                .map(state -> state.name)
                .collect(Collectors.joining(", ")));
        return current.getSuccessors().stream()
                .map(succ -> getStronglyConnectedComponents(start, succ, new HashSet<>(visited)))
                .reduce(new HashSet<>(), (setA, setB) -> {
                    setA.addAll(setB);
                    if(!setA.isEmpty()) setA.add(current);
                    return setA;
                });
    }

    private Set<Set<State>> filterDuplicates(Set<Set<State>> toFilter) {
        solutionInformation.add("Filtering out overlapping sets");
        List<Set<State>> original = new ArrayList<>(toFilter);
        Set<Set<State>> toRemove = new HashSet<>();
        for(int i = 0; i < original.size(); i++) {
            for(int j = 0; j < original.size(); j++) {
                if(i == j) continue;

                if(original.get(i).size() > original.get(j).size() && original.get(i).containsAll(original.get(j))) {
                    solutionInformation.add("Removing set " + original.get(j).stream()
                            .map(state -> state.name)
                            .collect(Collectors.joining(", ", "[", "]")) + " in favor of set " +
                                                    original.get(i).stream()
                                                            .map(state -> state.name)
                                                            .collect(Collectors.joining(", ", "[", "]")));
                    toRemove.add(original.get(j));
                } else if(original.get(j).size() > original.get(i).size() && original.get(j).containsAll(original.get(i))) {
                    solutionInformation.add("Removing set " + original.get(i).stream()
                            .map(state -> state.name)
                            .collect(Collectors.joining(", ", "[", "]")) + " in favor of set " +
                                                    original.get(j).stream()
                                                            .map(state -> state.name)
                                                            .collect(Collectors.joining(", ", "[", "]")));
                    toRemove.add(original.get(i));
                }
            }
        }

        original.removeAll(toRemove);
        return new HashSet<>(original);
    }
}

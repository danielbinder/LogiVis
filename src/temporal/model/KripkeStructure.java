package temporal.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class KripkeStructure {

    private static final int STRUCTURE_ELEM_LEN = 4;
    private static final int SUB_STRUCTURE_SPLIT_LEN = 2;

    private final List<Transition> transitions = new ArrayList<>();
    private final List<State> initialStates = new ArrayList<>();
    private final List<State> states = new ArrayList<>();
    private final List<String> atoms = new ArrayList<>();

    public KripkeStructure(List<State> states,
                           List<State> initialStates,
                           List<Transition> transitions,
                           List<String> atoms) {
        this.states.addAll(states);
        this.initialStates.addAll(initialStates);
        this.transitions.addAll(transitions);
        this.atoms.addAll(atoms);
    }

    public KripkeStructure(String structureDefinition) {
        String [] structure = structureDefinition
                .replace(System.lineSeparator(), "")
                .replace("\n", "")
                .split(";");

        if(structure.length == STRUCTURE_ELEM_LEN) {
            String[] states = structure[0].replace(" ", "").split(",");
            String[] initialStates = structure[1].split(":");
            String[] transitions = structure[2].replace(" ", "").split(",");
            String[] stateAtomStructs = structure[3].split(",");

            Arrays.asList(states).forEach(stateName -> {
                State state = new State(stateName.trim());
                if (!getStates().contains(state)) getStates().add(state);
            });

            if(!initialStates[1].trim().isEmpty()) {
                String[] initialStateNames = initialStates[1].trim().split(",");
                Arrays.asList(initialStateNames).forEach(stateName -> {
                    getStates().forEach(state -> {
                        if (state.getStateName().equals(stateName.trim()) && !getInitialStates().contains(state)) {
                            getInitialStates().add(state);
                        }
                    });
                });
            }

            Arrays.asList(transitions).forEach(transitionStr -> {
                String[] parsedTransition = transitionStr.split(":");

                if (parsedTransition.length == SUB_STRUCTURE_SPLIT_LEN) {
                    String name = parsedTransition[0];
                    String [] parsedStates = parsedTransition[1].split("-");

                    if(parsedStates.length == SUB_STRUCTURE_SPLIT_LEN) {
                        Optional<State> from = findState(parsedStates[0]);
                        Optional<State> to = findState(parsedStates[1]);

                        if(from.isPresent() && to.isPresent()) {
                            Transition transition = new Transition(name, from.get(), to.get());
                            if(!getTransitions().contains(transition)) getTransitions().add(transition);
                        }
                    }
                }
            });

            Arrays.asList(stateAtomStructs).forEach(atomStr -> {
                String [] parsedAtomStruct = atomStr.split(":");

                if(parsedAtomStruct.length == SUB_STRUCTURE_SPLIT_LEN) {
                    String stateName = parsedAtomStruct[0].replace(" ", "");
                    String atomNames = parsedAtomStruct[1].trim();
                    List<String> stateAtoms = new ArrayList<>();

                    Arrays.asList(atomNames.split(" ")).forEach(atom -> {
                        if(atom != null && !atom.isEmpty()) stateAtoms.add(atom);
                    });

                    Optional<State> state = findState(stateName);
                    state.ifPresent(value -> value.setAtoms(stateAtoms));

                    stateAtoms.forEach(atom -> {
                        if(!getAtoms().contains(atom)) getAtoms().add(atom);
                    });
                }
            });
        }
    }

    public Optional<State> findState(String name) {
        return getStates().stream().filter(state -> state.getStateName().equals(name)).findFirst();
    }

    public List<Transition> getTransitions() { return transitions; }

    public List<State> getInitialStates() { return initialStates; }

    public List<State> getStates() { return states; }

    public List<String> getAtoms() { return atoms; }

    public String toModelString() {
        String result = "";

        result += states.stream().map(State::getStateName).collect(Collectors.joining(","));
        result += ";\ninitial: ";
        result += initialStates.stream().map(State::getStateName).collect(Collectors.joining(","));
        result += ";\n";
        AtomicInteger tCount = new AtomicInteger();
        result += transitions.stream()
                .map(t -> "t" + tCount.getAndIncrement() + " : " + t.getFromState() + " - " + t.getToState())
                .collect(Collectors.joining(",\n"));
        result += ";\n";
        result += states.stream()
                .map(s -> s + " : " + String.join(" ", s.getAtoms()))
                .collect(Collectors.joining(",\n"));

        return result + ";";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("States (with atoms): ");
        sb.append(System.lineSeparator());
        List<String> stateStrings = new ArrayList<>();
        getStates().forEach(state -> {
            stateStrings.add(String.format("%s: %s", state.getStateName(),
                    String.join(", ", state.getAtoms())));
        });
        sb.append(String.join(System.lineSeparator(), stateStrings));
        sb.append(System.lineSeparator());

        sb.append("Initial state(s): ");
        if(!getInitialStates().isEmpty()) {
            sb.append(getInitialStates().stream().map(State::getStateName).collect(Collectors.joining(", ")));
        } else {
            sb.append("none");
        }
        sb.append(System.lineSeparator());

        sb.append("Transitions: ");
        sb.append(System.lineSeparator());
        List<String> transitionStrings = new ArrayList<>();
        getTransitions().forEach(transition -> {
            transitionStrings.add(String.format("%s: %s -> %s", transition.getTransitionName(),
                    transition.getFromState().getStateName(), transition.getToState().getStateName()));
        });
        sb.append(String.join(System.lineSeparator(), transitionStrings));
        return sb.toString();
    }
}

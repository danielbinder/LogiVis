package algorithmTester;

import marker.AlgorithmImplementation;
import model.finite.FiniteAutomaton;
import model.finite.State;
import util.Pair;

import java.util.Set;
import java.util.stream.Collectors;

public class YourImplementation implements AlgorithmImplementation {
    private void EXPLANATION() {
        // Create new FiniteAutomaton like this
        FiniteAutomaton automaton = new FiniteAutomaton();
        // Or create States
        State s1 = new State();
        // And add it to the model
        automaton.add(s1);

        // A FiniteAutomaton IS a Set<State>, so you can go through all States like this
        for(State s : automaton) {
            if(s.isInitialState) System.out.println(s.name);
        }
        // Or with a stream
        automaton.stream()
                .filter(node -> node.isInitialState)
                .map(node -> node.name)
                .forEach(System.out::println);

        // You can get individual States by their names
        State s2 = automaton.get("s2");
        // You can also check if the automaton contains a state with
        boolean s3Exists = automaton.contains("s3");
        // If you don't know if the state exists, you can use getOrCreate
        // to get one with this name, or create one if it doesn't exist yet
        State s3 = automaton.getOrCreate("s3");
        // Add a successor with, it returns the original state, here it returns s2 (for better usage in streams)
        s2.addSuccessor("a", s3);
        // Or an arbitrary amount of Set<State>
        s2.addSuccessors("b", s1.getSuccessors(), s2.getSuccessors(), s3.getSuccessors());

        // Each State has the following properties which you can all access and modify
        // The node name
        String name = s2.name;
        // If it is an initial node
        boolean isInitialNode = s2.isInitialState;
        // If it is a final node
        boolean isFinalNode = s2.isFinalState;
        // If it is an encoding start point (you only need this for encoding a model into a boolean formula)
        boolean isEncodingStartPoint = s2.isEncodingStart;
        // If it is an encoding start point (you only need this for encoding a model into a boolean formula)
        boolean isEncodingEndPoint = s2.isEncodingEnd;
        // You can get all successor properties with
        Set<String> properties = s2.getSuccessorProperties();
        // You can get all successors for a specific property with
        Set<State> aSuccessors = s2.getSuccessorsFor("a");
        // You can also get all successors
        Set<State> allSuccessors = s2.getSuccessors();
        // And to make checking for a specific successor faster, use
        boolean s2s3 = s2.hasSuccessor(s3);
        // Or if it has a specific property
        boolean s2a = s2.hasProperty("a");


        // You can loop through successors using
        for(State successor : s2.getSuccessors())
            System.out.println(successor.name);
        // Or
        for(String property : s2.getSuccessorProperties())
            System.out.println(s2.getSuccessorsFor(property));
        // You could also use a stream
        Set<String> successorNames = s2.getSuccessors().stream()
                .map(node -> node.name)
                .collect(Collectors.toSet());

        // You can get initial States with
        Set<State> initial = automaton.getInitialStates();
        // Same goes for final States
        Set<State> finals = automaton.getFinalStates();
        // Encoding starts
        Set<State> encodingStarts = automaton.getEncodingStarts();
        // Encoding ends
        Set<State> encodingEnds = automaton.getEncodingEnds();
        // Or any other filtered subset automaton
        Set<State> statesWithOneSuccessor = automaton.filter(state -> state.getSuccessors().size() == 1);

        // If you want to check if ANY state meets a condition, you can use
        boolean hasASuccessor = automaton.anyMatch(state -> state.hasProperty("a"));
        // Or if ALL states match
        boolean allHaveASuccessor = automaton.allMatch(state -> state.hasProperty("a"));
        // Or if NONE states match
        boolean noneHaveASuccessor = automaton.noneMatch(state -> state.hasProperty("a"));

        // You can get the automatons alphabet with
        Set<String> alphabet = automaton.getAlphabet();

        // You can map States to whatever you want
        Set<String> names = automaton.map(state -> state.name);
        // Or if you have multiple dimensions of streams, flatmap them
        Set<State> aSuccessorsOfAllStates = automaton.flatMap(state -> state.getSuccessorsFor("a").stream());
        // If you want a FiniteAutomaton back, you can use mapStates
        FiniteAutomaton allToS1 = automaton.mapStates(state -> state.addSuccessor("a", s1));
        // Or if you have multiple dimensions of streams
        FiniteAutomaton onlySuccessorStates = automaton.flatMapStates(state -> state.getSuccessors().stream());
        // You can also collect a Stream<State> to a FiniteAutomaton using
        FiniteAutomaton sameAutomaton = automaton.stream().collect(FiniteAutomaton.collector());

        // If you want to combine multiple state names, use
        // It automatically sorts the names, so you don't have weird duplicates
        String combinedName = combinedName(s1, s2, s3);
        // You can also create a combinedName from a Collection
        String anotherCombinedName = combinedName(Set.of(s1, s2));
        // Sometimes you shouldn't sort the states, so there is also
        String s2s1s3 = unsortedCombinedName(s2, s1, s3);

        //Feel free to use the Pair class if you need it
         Pair<State, State> pair = Pair.of(s1, s2);
         State left = pair.left;
         State right = pair.right;

         // You can clone an automaton if you want to work on it in place
        FiniteAutomaton clonedVersion = automaton.clone();

        // The following will show up in the frontend as warning (orange)
        System.out.println("some warning");
        // While Exceptions show up as errors in the frontend (red)
        throw new IllegalStateException("Some exception");
        // In dev mode, they will be printed to the console instead
    }

    @Override
    public boolean isDeterministic(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'isDeterministic' not implemented yet!");
    }

    @Override
    public boolean isComplete(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'isComplete' not implemented yet!");
    }

    @Override
    public boolean isEquivalent(FiniteAutomaton automaton1, FiniteAutomaton automaton2) {
        throw new IllegalStateException("Algorithm 'isEquivalent' is not implemented yet!");
    }

    @Override
    public FiniteAutomaton toProductAutomaton(FiniteAutomaton automaton1, FiniteAutomaton automaton2) {
        throw new IllegalStateException("Algorithm 'toProductAutomaton' not implemented yet!");
    }

    @Override
    public FiniteAutomaton toPowerAutomaton(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'toPowerAutomaton' not implemented yet!");
    }

    @Override
    public FiniteAutomaton toComplementAutomaton(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'toComplementAutomaton' not implemented yet!");
    }

    @Override
    public FiniteAutomaton toSinkAutomaton(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'toSinkAutomaton' not implemented yet!");
    }

    @Override
    public FiniteAutomaton toOracleAutomaton(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'toOracleAutomaton' not implemented yet!");
    }

    @Override
    public FiniteAutomaton toOptimisedOracleAutomaton(FiniteAutomaton automaton) {
        throw new IllegalStateException("Algorithm 'toOptimisedOracleAutomaton' not implemented yet!");
    }
}

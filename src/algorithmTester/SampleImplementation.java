package algorithmTester;

import marker.AlgorithmImplementation;
import model.finite.FiniteAutomaton;
import model.finite.State;

import java.util.*;

public class SampleImplementation implements AlgorithmImplementation {
    @Override
    public boolean isDeterministic(FiniteAutomaton automaton) {
        // Example of a solution without streams ('CTRL + /' to comment in/out highlighted lines)
//        boolean isDeterministic = automaton.getInitialStates().size() <= 1;
//        for(State state : automaton) {
//            for(String property : state.getSuccessorProperties()) {
//                if(state.getSuccessorsFor(property).size() > 1) isDeterministic = false;
//            }
//        }
//
//        return isDeterministic;
        return automaton.getInitialStates().size() <= 1 && automaton
                .allMatch(state -> state.getSuccessorProperties().stream()
                        .map(state::getSuccessorsFor)
                        .allMatch(succ -> succ.size() <= 1));
    }

    @Override
    public boolean isComplete(FiniteAutomaton automaton) {
        int alphabetSize = automaton.getAlphabet().size();

        return automaton
                .allMatch(state -> state.getSuccessorProperties().size() == alphabetSize &&
                        state.getSuccessorProperties().stream()
                                .map(state::getSuccessorsFor)
                                .noneMatch(Set::isEmpty));
    }
    @Override
    public FiniteAutomaton toProductAutomaton(FiniteAutomaton automaton1, FiniteAutomaton automaton2) {
        FiniteAutomaton power = new FiniteAutomaton();

         automaton1.getInitialStates()
                 .forEach(s1 -> automaton2.getInitialStates()
                         .forEach(s2 -> recursiveProductAutomaton(s1, s2, power)));

         return power;
    }

    private void recursiveProductAutomaton(State s1, State s2, FiniteAutomaton product) {
        // S = S1 x S2
        State productstate = product.getOrCreate(combinedName(s1, s2));
        // I = I1 x I2
        if(s1.isInitialState && s2.isInitialState) productstate.isInitialState = true;
        // F = F1 x F2
        if(s1.isFinalState && s2.isFinalState) productstate.isFinalState = true;

        product.add(productstate);

        for(String prop1 : s1.getSuccessorProperties()){
            for(State succ1 : s1.getSuccessorsFor(prop1)) {
                for(State succ2 : s2.getSuccessorsFor(prop1)) {
                    if(productstate.getSuccessorsFor(prop1).contains(product.getOrCreate(combinedName(succ1, succ2)))) continue;

                    productstate.addSuccessor(prop1, product.getOrCreate(combinedName(succ1, succ2)));

                    recursiveProductAutomaton(succ1, succ2, product);
                }
            }
        }

    }

    @Override
    public FiniteAutomaton toPowerAutomaton(FiniteAutomaton automaton) {
        if(automaton.isDeterministic() && automaton.isComplete()) return automaton;

        FiniteAutomaton power = recursivePowerAutomaton(automaton, new FiniteAutomaton(), automaton.getInitialStates());

        if(power.contains("sink")) {
            State sink = power.get("sink");
            automaton.getAlphabet()
                    .forEach(letter -> sink.addSuccessor(letter, sink));
        }

        return power;
    }

    private FiniteAutomaton recursivePowerAutomaton(FiniteAutomaton original, FiniteAutomaton power, Set<State> current) {
        State powerstate = power.getOrCreate(combinedName(current));

        powerstate.isInitialState = powerstate.name.equals(combinedName(original.getInitialStates()));
        powerstate.isFinalState = original
                .filter(current::contains)
                .anyMatch(state -> state.isFinalState);

        for(String prop : original.getAlphabet()) {
            Set<State> successors = original
                    .filter(current::contains)
                    .flatMap(state -> state.getSuccessorsFor(prop).stream());

            if(successors.isEmpty()) powerstate.addSuccessor(prop, power.getOrCreate("sink"));
            else if(powerstate.getSuccessorsFor(prop).contains(power.getOrCreate(combinedName(successors)))) continue;
            else {
                // link to successors
                powerstate.addSuccessor(prop, power.get(combinedName(successors)));

                // create successors
                recursivePowerAutomaton(original, power, successors);
            }
        }

        return power;
    }

    @Override
    public FiniteAutomaton toComplementAutomaton(FiniteAutomaton automaton) {
        if(!automaton.isDeterministic()) automaton = toPowerAutomaton(automaton);
        else if(!automaton.isComplete()) automaton = toSinkAutomaton(automaton);

        automaton.forEach(state -> state.isFinalState = !state.isFinalState);

        return automaton;
    }

    @Override
    public FiniteAutomaton toSinkAutomaton(FiniteAutomaton automaton) {
        if(automaton.isComplete()) return automaton;

        FiniteAutomaton sink = new FiniteAutomaton();
        State sinkstate = sink.getOrCreate("sink");
        // Add all successors to self
        Set<String> alphabet = automaton.getAlphabet();
        alphabet.forEach(input -> sinkstate.addSuccessor(input, sinkstate));

        // Add all states from 'automaton' as they are
        sink.addAll(automaton);

        for(State state : automaton)
            for(String input : alphabet)
                if(!state.getSuccessorProperties().contains(input)) state.addSuccessor(input, sinkstate);

        return sink;
    }

    @Override
    public FiniteAutomaton toOracleAutomaton(FiniteAutomaton automaton) {
        FiniteAutomaton oracle = new FiniteAutomaton();

        for(State state : automaton) {
            State oraclestate = new State();
            oraclestate.name = state.name;
            oraclestate.isInitialState = state.isInitialState;
            oraclestate.isFinalState = state.isFinalState;

            for(String property : state.getSuccessorProperties())
                for(State successor : state.getSuccessorsFor(property))
                    oraclestate.addSuccessor(property + successor.name, successor);

            oracle.add(oraclestate);
        }

        return oracle;
    }

    @Override
    public FiniteAutomaton toOptimisedOracleAutomaton(FiniteAutomaton automaton) {
        FiniteAutomaton optimisedOracle = new FiniteAutomaton();

        for(State state : automaton) {
            State optimisedOraclestate = new State();
            optimisedOraclestate.name = state.name;
            optimisedOraclestate.isInitialState = state.isInitialState;
            optimisedOraclestate.isFinalState = state.isFinalState;

            for(String prop : state.getSuccessorProperties()) {
                int i = 0;
                for(State successor : state.getSuccessorsFor(prop))
                    optimisedOraclestate.addSuccessor(prop + i++, successor);
            }

            optimisedOracle.add(optimisedOraclestate);
        }

        return optimisedOracle;
    }
}

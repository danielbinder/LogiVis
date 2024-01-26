package model.variant.finite;

import marker.Generator;

import java.util.ArrayList;
import java.util.List;

public class FiniteAutomatonGenerator implements Generator {
    public static FiniteAutomaton generate(int nodes, int initialNodes, int finalNodes, int alphabetSize,
                                           int minSuccessors, int maxSuccessors, boolean allReachable) {
        if(initialNodes < 0) Generator.error("The minimum amount of initial nodes needs to be at least 0!");
        if(initialNodes > nodes) Generator.error("The amount of initial nodes can't be larger than the amount of nodes!");
        if(finalNodes < 0) Generator.error("The minimum amount of final nodes needs to be at least 0!");
        if(finalNodes > nodes) Generator.error("The amount of final nodes can't be larger than the amount of nodes!");
        if(alphabetSize < 1 && nodes > 1 && minSuccessors > 0)
            Generator.error("The minimum alphabet size for more than 1 node needs to be at least 1 if minSuccessors is larger than 0!");
        if(alphabetSize < 1 && nodes > 1 && allReachable)
            Generator.error("The minimum alphabet size for more than 1 node needs to be at least 1 if all States should be reachable!");
        if(alphabetSize > maxSuccessors * nodes) Generator.error(maxSuccessors * nodes + " transitions cannot accommodate an alphabet size of " + alphabetSize);
        if(minSuccessors < 0) Generator.error("The minimum amount of successors needs to be at least 0!");
        if(minSuccessors > nodes) Generator.error("The amount of min successors can't be larger than the amount of nodes!");
        if(maxSuccessors < 0) Generator.error("The maximum amount of successors needs to be at least 0!");
        if(maxSuccessors > nodes) Generator.error("The amount of max successors can't be larger than the amount of nodes!");
        if(minSuccessors > maxSuccessors) Generator.error("The amount of min successors can't be larger than the amount of max successors!");
        if(allReachable && maxSuccessors < 1 && initialNodes < nodes)
            Generator.error("It is impossible to make all nodes reachable with this configuration!\nEither maxSuccessors need to be > 0 or initialNodes >= nodes!");

        FiniteAutomaton fa = new FiniteAutomaton();
        for(int i = 0; i < nodes; i++) fa.add(new State("s" + i));

        for(int i : Generator.pickRandom(nodes, initialNodes)) fa.get("s" + i).isInitialState = true;
        for(int i : Generator.pickRandom(nodes, finalNodes)) fa.get("s" + i).isFinalState = true;

        List<String> alphabet = Generator.generateVariableNames(alphabetSize);
        int currLetter = 0;
        boolean allLettersUsed = false;

        if(allReachable && nodes > 1) {
            List<State> reachable = new ArrayList<>(fa.stream().filter(state -> state.isInitialState)
                                                                 .toList());
            List<State> unreachable = new ArrayList<>(fa.stream().filter(state -> !state.isInitialState)
                                                                   .toList());

            if(initialNodes == 0) {
                State randomUnreachable = unreachable.get(Generator.pickRandom(unreachable.size()));

                reachable.add(randomUnreachable);
                unreachable.remove(randomUnreachable);
            }


            while(!unreachable.isEmpty()) {
                State firstUnreachable = unreachable.remove(0);
                reachable.get(Generator.pickRandom(reachable.size()))
                        .addSuccessor(alphabet.get(currLetter) ,firstUnreachable);
                if(allLettersUsed) currLetter = Generator.pickRandom(alphabet.size());
                else if(currLetter + 1 >= alphabet.size()) {
                    currLetter = 0;
                    allLettersUsed = true;
                } else currLetter++;
                reachable.add(firstUnreachable);
            }
        }

        for(State s : fa) {
            if(maxSuccessors - s.getSuccessors().size() > 0) {
                int successors = rand.nextInt(Math.max(0, minSuccessors - s.getSuccessors().size()),
                                              Math.max(0, maxSuccessors + 1 - s.getSuccessors().size()));
                List<Integer> chosenSuccessors = Generator.pickRandom(nodes, successors);

                for(int succ : chosenSuccessors) {
                    s.addSuccessor(alphabet.get(currLetter), fa.get("s" + succ));
                    if(allLettersUsed) currLetter = Generator.pickRandom(alphabet.size());
                    else if(currLetter + 1 >= alphabet.size()) {
                        currLetter = 0;
                        allLettersUsed = true;
                    } else currLetter++;
                }
            }
        }

        return fa;
    }
}

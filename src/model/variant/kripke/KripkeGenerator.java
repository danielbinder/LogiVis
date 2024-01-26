package model.variant.kripke;

import marker.Generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KripkeGenerator implements Generator {
    public static KripkeStructure generate(int nodes, int initialNodes, int variables,
                                           int minSuccessors, int maxSuccessors, boolean allReachable) {
        if(initialNodes < 0) Generator.error("The minimum amount of initial nodes needs to be at least 0!");
        if(initialNodes > nodes) Generator.error("The amount of initial nodes can't be larger than the amount of nodes!");
        if(Math.pow(2, variables) < nodes)
            Generator.error(variables + " variables can only have " + (int) Math.pow(2, variables) + " assignments, but there are " + nodes + " nodes!");
        if(minSuccessors < 0) Generator.error("The minimum amount of successors needs to be at least 0!");
        if(minSuccessors > nodes) Generator.error("The amount of min successors can't be larger than the amount of nodes!");
        if(maxSuccessors < 0) Generator.error("The maximum amount of successors needs to be at least 0!");
        if(maxSuccessors > nodes) Generator.error("The amount of max successors can't be larger than the amount of nodes!");
        if(minSuccessors > maxSuccessors) Generator.error("The amount of min successors can't be larger than the amount of max successors!");
        if(allReachable && maxSuccessors < 1 && initialNodes < nodes) Generator.error("It is impossible to make all nodes reachable with this configuration!\nEither maxSuccessors need to be > 0 or initialNodes >= nodes!");

        KripkeStructure ks = new KripkeStructure();
        for(int i = 0; i < nodes; i++) ks.add(new KripkeNode("n" + i));

        for(int i : Generator.pickRandom(nodes, initialNodes)) ks.get(i).isInitialNodeNode = true;
        ks.addStateMaps(generateRandomStateMaps(variables, nodes));

        if(allReachable && nodes > 1) {
            List<KripkeNode> reachable = new ArrayList<>(ks.stream()
                                                                 .filter(kn -> kn.isInitialNodeNode)
                                                                 .toList());
            List<KripkeNode> unreachable = new ArrayList<>(ks.stream()
                                                                   .filter(kn -> !kn.isInitialNodeNode)
                                                                   .toList());

            if(initialNodes == 0) {
                KripkeNode randomUnreachable = unreachable.get(Generator.pickRandom(unreachable.size()));

                reachable.add(randomUnreachable);
                unreachable.remove(randomUnreachable);
            }

            while(!unreachable.isEmpty()) {
                KripkeNode firstUnreachable = unreachable.remove(0);
                reachable.get(Generator.pickRandom(reachable.size()))
                        .successors.add(firstUnreachable);
                reachable.add(firstUnreachable);
            }
        }

        for(KripkeNode n : ks) {
            if(maxSuccessors - n.successors.size() > 0) {
                int successors = rand.nextInt(Math.max(0, minSuccessors - n.successors.size()),
                                              Math.max(0, maxSuccessors + 1 - n.successors.size()));
                List<Integer> chosenSuccessors = Generator.pickRandom(nodes, successors);

                for(int succ : chosenSuccessors) n.successors.add(ks.get(succ));
            }
        }

        return ks;
    }

    private static List<Map<String, Boolean>> generateRandomStateMaps(int variables, int amount) {
        List<Map<String, Boolean>> stateMaps = new ArrayList<>();

        for(int i = 0; i < Math.pow(2, variables); i++) {
            StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(i));

            // Add '0'-padding in front
            while(binaryString.length() < variables) binaryString.insert(0, "0");

            Map<String, Boolean> stateMap = new HashMap<>();
            int v = 0;
            for(Character c : binaryString.toString()
                    .toCharArray())
                stateMap.put(String.valueOf((char) ('a' + v++)), c == '1');

            stateMaps.add(stateMap);
        }

        List<Integer> pickedIndices = Generator.pickRandom(stateMaps.size(), amount);

        return stateMaps.stream()
                .filter(sm -> pickedIndices.contains(stateMaps.indexOf(sm)))
                .toList();
    }
}
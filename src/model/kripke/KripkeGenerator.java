package model.kripke;

import marker.Generator;

import java.util.*;

public class KripkeGenerator implements Generator {
    private static final Random rand = new Random();

    /**
     * @param paramString format: nodes_initialNodes_variables_minSuccessors_maxSuccessors_allStatesReachable
     * @return Kripke Structure
     */
    public static KripkeStructure generate(String paramString, int maxRegeneration) {
        Random rand = new Random();
        KripkeStructure ks = null;

        boolean regenerate = true;
        int regenerated = 0;

        String[] params = paramString.split("_");
        int nodes = Integer.parseInt(params[0]);
        int initialNodes = Integer.parseInt(params[1]);
        if(initialNodes < 0) error("The minimum amount of initial nodes needs to be at least 0!");
        if(initialNodes > nodes) error("The amount of initial nodes can't be larger than the amount of nodes!");
        int variables = Integer.parseInt(params[2]);
        if(Math.pow(2, variables) < nodes)
            error(variables + " variables can only have " + (int) Math.pow(2, variables) + " assignments, but there are " + nodes + " nodes!");
        int minSuccessors = Integer.parseInt(params[3]);
        if(minSuccessors < 0) error("The minimum amount of successors needs to be at least 0!");
        if(minSuccessors > nodes) error("The amount of min successors can't be larger than the amount of nodes!");
        int maxSuccessors = Integer.parseInt(params[4]);
        if(maxSuccessors < 0) error("The maximum amount of successors needs to be at least 0!");
        if(maxSuccessors > nodes) error("The amount of max successors can't be larger than the amount of nodes!");
        if(minSuccessors > maxSuccessors) error("The amount of min successors can't be larger than the amount of max successors!");
        boolean allStatesReachable = Boolean.parseBoolean(params[5]);

        while(regenerate && regenerated < maxRegeneration) {
            ks = new KripkeStructure();
            for(int i = 0; i < nodes; i++) ks.add(new KripkeNode("n" + i));

            for(int i : pickRandom(nodes, initialNodes)) ks.get(i).isInitialNodeNode = true;
            ks.addStateMaps(generateRandomStateMaps(variables, nodes));

            for(KripkeNode n : ks) {
                int successors = rand.nextInt(minSuccessors, maxSuccessors + 1);
                List<Integer> chosenSuccessors = pickRandom(nodes, successors);

                for(int succ : chosenSuccessors) n.successors.add(ks.get(succ));
            }

            if(allStatesReachable) {
                List<KripkeNode> reachable = new ArrayList<>();
                ks.stream().filter(kn -> kn.isInitialNodeNode).forEach(kn -> walk(kn, reachable));

                if(nodes == reachable.size()) regenerate = false;
                else regenerated++;
            } else break;
        }

        if(regenerate)
            System.out.println("Tried " + maxRegeneration + " times, but reachability was not achieved for all states!");

        return ks;
    }

    /**
     * Walks the KripkeStructure recursively to check if every node is reachable
     * by adding every visited node to the reachabilityList
     * @param current node
     * @param reachabilityList of walked nodes
     */
    private static void walk(KripkeNode current, List<KripkeNode> reachabilityList) {
        if(reachabilityList.contains(current)) return;

        reachabilityList.add(current);
        for(KripkeNode kn : current.successors) walk(kn, reachabilityList);
    }

    private static List<Integer> pickRandom(int bound, int amount) {
        assert bound >= amount;
        List<Integer> picks = new ArrayList<>();

        while(amount > 0) {
            int pick = rand.nextInt(bound) & Integer.MAX_VALUE;

            if(!picks.contains(pick)) {
                picks.add(pick);
                amount--;
            }
        }

        return picks;
    }

    private static List<Map<String, Boolean>> generateRandomStateMaps(int variables, int amount) {
        List<Map<String, Boolean>> stateMaps = new ArrayList<>();

        for(int i = 0; i < Math.pow(2, variables); i++) {
            StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(i));

            // Add '0'-padding in front
            while(binaryString.length() < variables) binaryString.insert(0, "0");

            Map<String, Boolean> stateMap = new HashMap<>();
            int v = 0;
            for(Character c : binaryString.toString().toCharArray()) stateMap.put(((char) ('a' + v++)) + "", c == '1');

            stateMaps.add(stateMap);
        }

        List<Integer> pickedIndices = pickRandom(stateMaps.size(), amount);

        return stateMaps.stream()
                .filter(sm -> pickedIndices.contains(stateMaps.indexOf(sm)))
                .toList();
    }

    private static void error(String message) {
        throw new IllegalArgumentException(message);
    }
}

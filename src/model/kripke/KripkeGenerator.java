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
        assert(1 <= initialNodes && initialNodes <= nodes);
        int variables = Integer.parseInt(params[2]);
        assert(Math.pow(2, variables) > nodes);     // ensure enough unique variable assignments
        int minSuccessors = Integer.parseInt(params[3]);
        assert(0 <= minSuccessors && minSuccessors <= nodes - 1);
        int maxSuccessors = Integer.parseInt(params[4]);
        assert(0 <= maxSuccessors && maxSuccessors <= nodes - 1 && minSuccessors <= maxSuccessors);
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
                Map<KripkeNode, Boolean> reachabilityMap = new HashMap<>();
                for(KripkeNode initial : ks.stream()
                        .filter(kn -> kn.isInitialNodeNode)
                        .toList())
                    walk(initial, reachabilityMap);

                if(nodes == reachabilityMap.keySet().size()) regenerate = false;
                else regenerated++;
            } else break;
        }

        return ks;
    }

    /**
     * Walks the KripkeStructure recursively to check if every node is reachable
     * by adding every visited node to the reachabilityMap
     * @param current node
     * @param reachabilityMap of walked nodes
     */
    private static void walk(KripkeNode current, Map<KripkeNode, Boolean> reachabilityMap) {
        if(reachabilityMap.containsKey(current)) return;

        reachabilityMap.put(current, true);
        for(KripkeNode kn : current.successors) walk(kn, reachabilityMap);
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
}

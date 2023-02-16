package generator;

import generator.kripke.KripkeNode;
import generator.kripke.KripkeStructure;

import java.util.*;

public class Generator {
    private static final Random rand = new Random();

    /**
     * @param paramString format: nodes;initialNodes;variables;minSuccessors;maxSuccessors;allStatesReachable
     * @return Kripke Structure
     */
    public static KripkeStructure generateKripkeStructure(String paramString) {
        Random rand = new Random();
        KripkeStructure ks = new KripkeStructure();
        String[] params = paramString.split("_");

        int nodes = Integer.parseInt(params[0]);
        for(int i = 0; i < nodes; i++) ks.add(new KripkeNode(i + ""));

        int initialNodes = Integer.parseInt(params[1]);
        for(int i : pickRandom(nodes, initialNodes)) ks.get(i).isInitialNodeNode = true;

        int variables = Integer.parseInt(params[2]);
        ks.addStateMaps(generateRandomStateMaps(variables, nodes));

        int minSuccessors = Integer.parseInt(params[3]);
        int maxSuccessors = Integer.parseInt(params[4]);
        for(KripkeNode n: ks) {
            int successors = rand.nextInt(minSuccessors, maxSuccessors);
            List<Integer> chosenSuccessors = pickRandom(nodes, successors);

            for(int succ : chosenSuccessors) n.successors.add(ks.get(succ));
        }

        boolean allStatesReachable = Boolean.parseBoolean(params[5]);
        if(allStatesReachable) {
            Map<String, Integer> reachabilityMap = new HashMap<>();

            for(KripkeNode n : ks) reachabilityMap.put(n.name, 0);

            for(KripkeNode n : ks)
                for(KripkeNode succ : n.successors)
                    reachabilityMap.put(succ.name, reachabilityMap.get(succ.name) + 1);
        }

        return ks;
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

package model.kripke;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KripkeNode {
    public final String name;
    public Map<String, Boolean> stateMap;
    public boolean isInitialNodeNode;
    public final Set<KripkeNode> successors;

    public KripkeNode(String name) {
        this.name = name;
        this.stateMap = new HashMap<>();
        this.isInitialNodeNode = false;
        this.successors = new HashSet<>();
    }

    /**
     * Does NOT do linking of nodes!
     */
    public static KripkeNode fromString(String node) {
        String[] parts = node.split(";");
        KripkeNode kn = new KripkeNode(parts[0]);

        String assignments = parts[1];
        for(String a : assignments.split("\\+")) {
            String[] var = a.split(":");
            kn.stateMap.put(var[0], var[1].equals("true"));
        }

        kn.isInitialNodeNode = parts[2].equals("true");

        return kn;
    }

    @Override
    public String toString() {
        return name + ";" +
                stateMap.entrySet()
                        .stream()
                        .map(e -> e.getKey() + ":" + e.getValue().toString())
                        .collect(Collectors.joining("+")) + ";" +
                isInitialNodeNode + ";" +
                successors.stream()
                        .map(n -> n.name)
                        .collect(Collectors.joining("+"));
    }
}

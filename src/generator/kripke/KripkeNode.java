package generator.kripke;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + ";");

        for(Map.Entry<String, Boolean> e : stateMap.entrySet()) {
            sb.append(e.getKey())
                    .append(":")
                    .append(e.getValue().toString())
                    .append("+");
        }
        sb.append(";");

        sb.append(isInitialNodeNode)
                .append(";");

        for(KripkeNode n : successors) {
            sb.append(n.name)
                    .append("+");
        }

        return sb.toString();
    }
}

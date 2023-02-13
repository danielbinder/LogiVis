package generator.kripke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KripkeStructure extends ArrayList<KripkeNode> {
    public void addStateMaps(List<Map<String, Boolean>> stateMaps) {
        int i = 0;

        for(KripkeNode n : this) n.stateMap = stateMaps.get(i);
    }

    @Override
    public String toString() {
        return stream()
                .map(KripkeNode::toString)
                .collect(Collectors.joining("_"));
    }
}

package model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelTracer {
    private final List<ModelNode> startNodes;
    private final List<ModelNode> goalNodes;
    private final Set<ModelNode> visited = new HashSet<>();
    private final int reachableNodes;

    public ModelTracer(Model model) {
        startNodes = model.stream().filter(n -> n.isEncodingStartPoint).toList();
        goalNodes = model.stream().filter(n -> n.isEncodingEndPoint).toList();

        reachableNodes = startNodes.stream()
                .map(this::walk)
                .reduce(0, Integer::sum);
    }

    public String trace() {
        if(startNodes.isEmpty()) throw new IllegalArgumentException("No start node defined for trace!");
        if(goalNodes.isEmpty()) throw new IllegalArgumentException("No goal node defined for trace!");

        visited.clear();
        return startNodes.stream()
                .map(this::trace)
                .filter(s -> !s.isBlank())
                .findAny()
                .orElse("No trace found!");
    }

    private String trace(ModelNode node) {
        if(visited.contains(node)) return "";

        visited.add(node);
        if(goalNodes.contains(node)) return node.name;

        String futureTrace = node.successors.keySet().stream()
                .map(this::trace)
                .filter(s -> !s.isBlank())
                .findAny()
                .orElse("");

        return futureTrace.isBlank() ? "" : node.name + " -> " + futureTrace;
    }

    /**
     * Uses depth-first search with iterative deepening.
     * Depth-first search returns the shortest node
     * Iterative deepening makes sure the search is not stuck in an endless loop
     * @return shortest node chain
     */
    public String shortestTrace() {
        if(startNodes.isEmpty()) throw new IllegalArgumentException("No start node defined for trace!");
        if(goalNodes.isEmpty()) throw new IllegalArgumentException("No goal node defined for trace!");

        visited.clear();
        for(int i = 0; visited.size() < reachableNodes; i++) {
            int finalI = i;
            String result = startNodes.stream()
                    .map(n -> shortestTrace(n, 0, finalI))
                    .filter(s -> !s.isBlank())
                    // min transitions
                    .min(Comparator.comparingLong(s -> s.chars().filter(c -> c == '>').count()))
                    .orElse("");

            if(!result.isBlank()) return result;
        }

        return "No trace found!";
    }

    private String shortestTrace(ModelNode node, int depth, int maxDepth) {
        if(depth > maxDepth) return "";

        visited.add(node);
        if(goalNodes.contains(node)) return node.name;

        String futureTrace = node.successors.keySet()
                .stream()
                .map(n -> shortestTrace(n, depth + 1, maxDepth))
                .filter(s -> !s.isBlank())
                // min transitions
                .min(Comparator.comparingLong(s -> s.chars().filter(c -> c == '>').count()))
                .orElse("");

        return futureTrace.isBlank() ? "" : node.name + " -> " + futureTrace;
    }

    /**
     * @param node to walk
     * @return amount of encountered nodes
     */
    private int walk(ModelNode node) {
        if(visited.contains(node)) return 0;

        visited.add(node);
        return 1 + node.successors.keySet().stream()
                .map(this::walk)
                .reduce(0, Integer::sum);
    }
}

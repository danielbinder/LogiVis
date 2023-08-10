package model.kripke;

import bool.parser.logicnode.LogicNode;
import servlet.Result;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KripkeStructure extends ArrayList<KripkeNode> {
    public KripkeNode get(String name) {
        return stream()
                .filter(kn -> kn.name.equals(name))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    public void addStateMaps(List<Map<String, Boolean>> stateMaps) {
        int i = 0;

        for(KripkeNode n : this) n.stateMap = stateMaps.get(i++);
    }

    public static KripkeStructure fromString(String structure) {
        KripkeStructure ks = new KripkeStructure();
        ks.addAll(Arrays.stream(structure.split("_"))
                          .map(KripkeNode::fromString)
                          .toList());

        // Linking of Nodes
        Arrays.stream(structure.split("_"))
                .forEach(node -> {
                    String[] parts = node.split(";");
                    if(parts.length >= 4) {
                        for(String succ : parts[3].split("\\+")) ks.get(parts[0]).successors.add(ks.get(succ));
                    }
                });

        return ks;
    }

    public KripkeTruthTable toKripkeTruthTable() {
        return new KripkeTruthTable(this);
    }

    public LogicNode toFormula(int steps) {
        return LogicNode.of(toFormulaString(steps));
    }

    public Result toFormulaStringWithResult(int steps) {
        return new Result(toFormulaString(steps));
    }

    public String toFormulaString(int steps) {
        return IntStream.range(0, steps).mapToObj(step -> stream()
                        .filter(kn -> kn.successors.size() > 0)
                        .map(kn -> "(" +
                                kn.stateMap.entrySet().stream()
                                .map(literal -> (literal.getValue() ? "" : "!") + literal.getKey() + step)
                                .collect(Collectors.joining(" & ", "(", ")")) +
                                " -> " +
                                kn.successors.stream()
                                        .map(succ -> succ.stateMap.entrySet().stream()
                                                .map(succLiteral -> (succLiteral.getValue() ? "" : "!") + succLiteral.getKey() + (step + 1))
                                                .collect(Collectors.joining(" & ", "(", ")")))
                                        .collect(Collectors.joining(" | ", "(", ")")) +
                                ")")
                        .collect(Collectors.joining(" &\n", "(", ")")))
                .collect(Collectors.joining(" &\n\n", "(", ")")) +
                (stream().anyMatch(kn -> kn.isEncodingStart)
                        ? "\n& " +
                        stream()
                                .filter(kn -> kn.isEncodingStart)
                                .map(kn -> kn.stateMap.entrySet().stream()
                                        .map(literal -> (literal.getValue() ? "" : "!") + literal.getKey() + "0")
                                        .collect(Collectors.joining(" & ", "(", ")")))
                                .collect(Collectors.joining(" | ", "(", ")"))
                        : "") +
                (stream().anyMatch(kn -> kn.isEncodingEnd)
                        ? " & " +
                        stream()
                                .filter(kn -> kn.isEncodingEnd)
                                .map(kn -> kn.stateMap.entrySet().stream()
                                        .map(literal -> (literal.getValue() ? "" : "!") + literal.getKey() + steps)
                                        .collect(Collectors.joining(" & ", "(", ")")))
                                .collect(Collectors.joining(" | ", "(", ")"))
                        : "");
    }

    public Map<String, List<String>> getStateTraceMap() {
        return stream().collect(Collectors.toMap(kn -> kn.name,
                                                 kn -> kn.stateMap.entrySet().stream()
                                                         .map(e -> (e.getValue() ? "" : "!") + e.getKey())
                                                         .toList()));
    }

    public static String resolveStateTraceMap(int maxVarIndex, Map<String, List<String>> stateTraceMap, String states) {
        return IntStream.range(0, maxVarIndex + 1)
                .mapToObj(i -> stateTraceMap.entrySet().stream()
                        .filter(e -> e.getValue().stream().allMatch(v -> states.contains(v + i) && !states.contains("!" + v + i)))
                        .findAny()
                        .map(Map.Entry::getKey)
                        .orElseThrow(NoSuchElementException::new))
                .collect(Collectors.joining(" -> "));
    }

    @Override
    public String toString() {
        return stream()
                .map(KripkeNode::toString)
                .collect(Collectors.joining("_"));
    }
}

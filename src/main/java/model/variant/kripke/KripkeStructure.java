package model.variant.kripke;

import bool.parser.logicnode.LogicNode;
import model.variant.ModelVariant;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KripkeStructure extends ArrayList<KripkeNode> implements ModelVariant {
    @Serial
    private static final long serialVersionUID = 6635546786082740679L;

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

    public LogicNode toFormula(boolean upUntil, int steps) {
        return LogicNode.of(toFormulaString(upUntil, steps));
    }

    public String toFormulaString(boolean upUntil, int steps) {
        return IntStream.range(0, steps).mapToObj(step -> stream()
                        .filter(kn -> !kn.successors.isEmpty())
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
                .collect(Collectors.joining(" &\n\n", "(", ")")) + "\n" +
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
                        IntStream.range(upUntil ? 0 : steps, steps + 1)
                                .mapToObj(i -> stream()
                                        .filter(kn -> kn.isEncodingEnd)
                                        .map(kn -> kn.stateMap.entrySet().stream()
                                                .map(literal -> (literal.getValue() ? "" : "!") + literal.getKey() + i)
                                                .collect(Collectors.joining(" & ", "(", ")")))
                                        .collect(Collectors.joining(" | ", "(", ")")))
                                .collect(Collectors.joining(" | ", "(", ")"))
                        : "");
    }

    @Override
    public String toString() {
        return stream()
                .map(KripkeNode::toString)
                .collect(Collectors.joining("_"));
    }
}

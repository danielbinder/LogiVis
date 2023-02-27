package generator.kripke;

import lexer.Lexer;
import parser.Parser;
import parser.logicnode.LogicNode;

import java.util.*;
import java.util.stream.Collectors;

public class KripkeStructure extends ArrayList<KripkeNode> {
    public KripkeNode get(String name) {
        return stream().filter(kn -> kn.name.equals(name)).findAny().orElseThrow(NoSuchElementException::new);
    }

    public void addStateMaps(List<Map<String, Boolean>> stateMaps) {
        int i = 0;

        for(KripkeNode n : this) n.stateMap = stateMaps.get(i++);
    }

    public String toFormulaString(int steps) {
        StringBuilder formula = new StringBuilder("true ");

        for(int i = 0; i < steps; i++) {
            for(KripkeNode n : this) {
                formula.append(" &\n(");

                for(Map.Entry<String, Boolean> e : n.stateMap.entrySet()) {
                    formula.append(e.getValue() ? "" : "!")
                            .append(e.getKey())
                            .append(i)
                            .append(" & ");
                }

                formula.append("true) -> (false ");

                for(KripkeNode succ : n.successors) {
                    formula.append("| (");

                    for(Map.Entry<String, Boolean> e : succ.stateMap.entrySet()) {
                        formula.append(e.getValue() ? "" : "!")
                                .append(e.getKey())
                                .append(i + 1)
                                .append(" & ");
                    }

                    formula.append("true) ");
                }

                formula.append(")");
            }
        }

        return formula.toString();
    }

    public LogicNode toFormula(int steps) {
        return new Parser().parse(Lexer.tokenize(toFormulaString(steps)));
    }

    public static KripkeStructure fromString(String structure) {
        KripkeStructure ks = new KripkeStructure();
        ks.addAll(Arrays.stream(structure.split("_"))
                          .map(KripkeNode::fromString)
                          .toList());

        // Linking of Nodes
        Arrays.stream(structure.split("_")).forEach(node -> {
            String[] parts = node.split(";");
            for(String succ : parts[3].split("\\+")) ks.get(parts[0]).successors.add(ks.get(succ));
        });

        return ks;
    }

    @Override
    public String toString() {
        return stream()
                .map(KripkeNode::toString)
                .collect(Collectors.joining("_"));
    }
}

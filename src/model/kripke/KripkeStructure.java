package model.kripke;

import bool.parser.logicnode.LogicNode;
import temporal.model.KripkeStruct;
import temporal.model.State;
import temporal.model.Transition;

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

    public KripkeStruct toOtherKripke() {
        List<State> states = new ArrayList<>();
        List<State> initial = new ArrayList<>();
        Map<KripkeNode, State> stateMap = new HashMap<>();
        for(KripkeNode kn : this) {
            State s = new State(kn.name);
            states.add(s);
            stateMap.put(kn, s);

            if(kn.isInitialNodeNode) initial.add(s);

            s.setAtoms(kn.stateMap.entrySet().stream()
                               .filter(Map.Entry::getValue)
                               .map(Map.Entry::getKey)
                               .toList());
        }

        List<Transition> transitions = new ArrayList<>();
        for(KripkeNode kn : this)
            for(KripkeNode kn2 : kn.successors)
                transitions.add(new Transition(stateMap.get(kn), stateMap.get(kn2)));

        List<String> atoms = get(0).stateMap.keySet().stream().toList();

        return new KripkeStruct(states, initial, transitions, atoms);
    }

    public KripkeTruthTable toKripkeTruthTable() {
        return new KripkeTruthTable(this);
    }

    public LogicNode toFormula(int steps) {
        return LogicNode.of(toFormulaString(steps));
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

    @Override
    public String toString() {
        return stream()
                .map(KripkeNode::toString)
                .collect(Collectors.joining("_"));
    }
}

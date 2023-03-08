package generator.kripke;

import lexer.Lexer;
import parser.Parser;
import parser.logicnode.LogicNode;
import temporal.model.State;
import temporal.model.Transition;

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
                formula.append(" &\n((");

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

                formula.append("))");
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

    public temporal.model.KripkeStructure toOtherKripke() {
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

        return new temporal.model.KripkeStructure(states, initial, transitions, atoms);
    }

    @Override
    public String toString() {
        return stream()
                .map(KripkeNode::toString)
                .collect(Collectors.joining("_"));
    }
}

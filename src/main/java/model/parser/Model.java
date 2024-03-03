package model.parser;

import marker.ConceptRepresentation;
import model.interpreter.ModelTracer;
import model.variant.finite.FiniteAutomaton;
import model.variant.kripke.KripkeNode;
import model.variant.kripke.KripkeStructure;

import java.io.Serial;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Model extends HashSet<ModelNode> implements ConceptRepresentation {
    @Serial
    private static final long serialVersionUID = 3237311348082874119L;

    public Model() {}

    private Model(KripkeStructure ks) {
        ks.forEach(kn -> add(new ModelNode(kn)));
        ks.forEach(kn -> get(kn.name).isEncodingStartPoint = kn.isEncodingStart);
        ks.forEach(kn -> get(kn.name).isEncodingEndPoint = kn.isEncodingEnd);
        ks.forEach(kn -> kn.successors
                .forEach(succ -> get(kn.name).successors.put(get(succ.name), "")));
    }

    public static Model of(String modelString) {
        return ModelParser.parse(modelString);
    }

    public static Model of(KripkeStructure ks) {
        return new Model(ks);
    }

    public void addAll(ModelNode...modelNodes) {
        addAll(Arrays.stream(modelNodes).toList());
    }

    public ModelNode get(String name) {
        return stream()
                .filter(gn -> gn.name.equals(name))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    public boolean contains(String name) {
        return stream().anyMatch(gn -> gn.name.equals(name));
    }

    public KripkeStructure toKripkeStructure() {
        KripkeStructure ks = new KripkeStructure();
        forEach(gn -> ks.add(new KripkeNode(gn.name)));
        forEach(gn -> ks.get(gn.name).isInitialNodeNode = gn.isInitialNode);
        forEach(gn -> ks.get(gn.name).isEncodingStart = gn.isEncodingStartPoint);
        forEach(gn -> ks.get(gn.name).isEncodingEnd = gn.isEncodingEndPoint);
        forEach(gn -> gn.successors
                .forEach((key, value) -> ks.get(gn.name).successors.add(ks.get(key.name))));
        forEach(gn -> ks.get(gn.name).stateMap = Arrays.stream(gn.label.replaceAll("'.*?'", "").split(" "))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toMap(l -> l.startsWith("!") ? l.substring(1) : l,
                                          l -> !l.startsWith("!"))));

        return ks;
    }

    public FiniteAutomaton toFiniteAutomaton() {
        FiniteAutomaton automaton = new FiniteAutomaton();
        forEach(node -> automaton.add(node.toState()));

        forEach(node -> node.successors
                .forEach((succ, label) -> Arrays.stream(label.replaceAll("'.*?'", "").split(" "))
                        .forEach(prop -> automaton.get(node.name).addSuccessor(prop, automaton.get(succ.name)))));

        return automaton;
    }

    public ModelTracer toModelTracer() {
        return new ModelTracer(this);
    }

    public String toModelString() {
        return (!isEmpty()
                        ? "S = {" + stream()
                            .map(n -> n.name +
                                    (n.isEncodingStartPoint ? ">" : "") +
                                    (n.isEncodingEndPoint ? "<" : "") +
                                    (!n.label.isBlank() ? " [" + n.label + "]" : ""))
                            .collect(Collectors.joining(", ")) + "}"
                        : "") +
                (stream().anyMatch(n -> n.isInitialNode)
                        ? ("\nI = {" + stream()
                            .filter(n -> n.isInitialNode)
                            .map(n -> n.name)
                            .collect(Collectors.joining(", ")) + "}")
                        : "") +
                (stream().anyMatch(n -> !n.successors.isEmpty())
                        ? "\nT = {" + stream()
                            .filter(n -> !n.successors.isEmpty())
                            .map(n -> n.successors.entrySet().stream()
                                    .map(succ -> "(" + n.name + ", " + succ.getKey().name + ")" +
                                            (!succ.getValue().isBlank() ?" [" + succ.getValue() + "]" : ""))
                                    .collect(Collectors.joining(", ")))
                            .collect(Collectors.joining(", ")) + "}"
                        : "") +
                (stream().anyMatch(n -> n.isFinalNode)
                        ? ("\nF = {" + stream()
                        .filter(n -> n.isFinalNode)
                        .map(n -> n.name)
                        .collect(Collectors.joining(", ")) + "}")
                        : "");
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Model m) {
            return m.stream()
                    .allMatch(node -> contains(node.name) &&
                            Arrays.stream(get(node.name).label.split(" ")).collect(Collectors.toSet())
                                    .equals(Arrays.stream(m.get(node.name).label.split(" ")).collect(Collectors.toSet())) &&
                            get(node.name).isInitialNode == m.get(node.name).isInitialNode &&
                            get(node.name).isFinalNode == m.get(node.name).isFinalNode &&
                            get(node.name).isEncodingStartPoint == m.get(node.name).isEncodingStartPoint &&
                            get(node.name).isEncodingEndPoint == m.get(node.name).isEncodingEndPoint &&
                            get(node.name).successors.entrySet().stream()
                                    .collect(Collectors.toMap(e -> e.getKey().name,
                                                              e -> Arrays.stream(e.getValue().split(" "))
                                                                      .collect(Collectors.toSet())))
                                    .equals(m.get(node.name).successors.entrySet().stream()
                                                    .collect(Collectors.toMap(e -> e.getKey().name,
                                                                              e -> Arrays.stream(e.getValue().split(" "))
                                                                                      .collect(Collectors.toSet())))));

        } else return false;
    }

    @Override
    public String toString() {
        return toModelString();
    }
}

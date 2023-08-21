package model;

import model.kripke.KripkeNode;
import model.kripke.KripkeStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Model extends ArrayList<ModelNode> {
    public Model() {}

    private Model(KripkeStructure ks) {
        ks.forEach(kn -> add(new ModelNode(kn)));
        ks.forEach(kn -> get(kn.name).isEncodingStartPoint = kn.isEncodingStart);
        ks.forEach(kn -> get(kn.name).isEncodingEndPoint = kn.isEncodingEnd);
        ks.forEach(kn -> kn.successors
                .forEach(succ -> get(kn.name).successors.put(get(succ.name), "")));
    }

    public static Model of(String modelString) {
        return new ModelParser().parse(modelString);
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
        forEach(gn -> ks.get(gn.name).isInitialNodeNode = gn.isInitialNodeNode);
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

    public ModelTracer toModelTracer() {
        return new ModelTracer(this);
    }

    public String toModelString() {
        return "S = {" + stream()
                .map(n -> n.name +
                        (n.isEncodingStartPoint ? ">" : "") +
                        (n.isEncodingEndPoint ? "<" : "") +
                        (!n.label.isBlank() ? " [" + n.label + "]" : ""))
                .collect(Collectors.joining(", ")) + "}\n" +
                "I = {" + stream()
                .filter(n -> n.isInitialNodeNode)
                .map(n -> n.name)
                .collect(Collectors.joining(", ")) + "}\n" +
                "T = {" + stream()
                .map(n -> n.successors.entrySet().stream()
                        .map(succ -> "(" + n.name + ", " + succ.getKey().name +
                                (!succ.getValue().isBlank() ?" [" + succ.getValue() + "]" : "") + ")")
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(", ")) + "}\n";
    }

    @Override
    public String toString() {
        return stream().map(ModelNode::toString).collect(Collectors.joining("\n"));
    }
}

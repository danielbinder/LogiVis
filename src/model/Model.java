package model;

import model.kripke.KripkeNode;
import model.kripke.KripkeStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Model extends ArrayList<ModelNode> {
    public Model() {}

    public Model(KripkeStructure ks) {
        ks.forEach(kn -> add(new ModelNode(kn)));

        ks.forEach(kn -> kn.successors
                .forEach(succ ->
                        forEach(gn -> gn.successors.put(get(succ.name), ""))));
    }

    public static Model of(String modelString) {
        return new ModelParser().parse(modelString);
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
        forEach(gn -> gn.successors
                .forEach((key, value) -> ks.get(gn.name).successors.add(ks.get(key.name))));
        forEach(gn -> ks.get(gn.name).stateMap = Arrays.stream(gn.label.split(" "))
                .collect(Collectors.toMap(l -> l.startsWith("!") ? l.substring(1) : l,
                                          l -> !l.startsWith("!"))));

        return ks;
    }

    @Override
    public String toString() {
        return stream().map(ModelNode::toString).collect(Collectors.joining("\n"));
    }
}

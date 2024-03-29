package model.parser;

import model.variant.finite.State;
import model.variant.kripke.KripkeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModelNode {

    public String name;
    public boolean isInitialNode;
    public boolean isFinalNode;
    public String label = "";
    public boolean isEncodingStartPoint;
    public boolean isEncodingEndPoint;
    /** Map<Successor, TransitionLabel> */
    public final Map<ModelNode, String> successors = new HashMap<>();

    public ModelNode(String name) {
        this.name = name;
        isInitialNode = false;
        isFinalNode = false;
        isEncodingStartPoint = false;
        isEncodingEndPoint = false;
    }

    public ModelNode(String name, boolean isInitialNodeNode, boolean isFinalNode, String label) {
        this(name);
        this.isInitialNode = isInitialNodeNode;
        this.isFinalNode = isFinalNode;
        this.label = label;
        isEncodingStartPoint = false;
        isEncodingEndPoint = false;
    }

    public ModelNode(KripkeNode kn) {
        this(kn.name,
             kn.isInitialNodeNode,
             false,
             kn.stateMap.entrySet().stream()
                     .map(e -> (e.getValue() ? "" : "!") + e.getKey())
                     .collect(Collectors.joining(" ")));
    }

    public State toState() {
        State bn = new State();
        bn.name = name;
        bn.isInitialState = isInitialNode;
        bn.isFinalState = isFinalNode;
        bn.isEncodingStart = isEncodingStartPoint;
        bn.isEncodingEnd = isEncodingEndPoint;

        return bn;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        ModelNode modelNode = (ModelNode) o;

        if(isInitialNode != modelNode.isInitialNode) return false;
        if(isFinalNode != modelNode.isFinalNode) return false;
        if(isEncodingStartPoint != modelNode.isEncodingStartPoint) return false;
        if(isEncodingEndPoint != modelNode.isEncodingEndPoint) return false;
        if(!Objects.equals(name, modelNode.name)) return false;
        if(!Objects.equals(label, modelNode.label)) return false;
        return successors.equals(modelNode.successors);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (isInitialNode ? 1 : 0);
        result = 31 * result + (isFinalNode ? 1 : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (isEncodingStartPoint ? 1 : 0);
        result = 31 * result + (isEncodingEndPoint ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return name +
                (isInitialNode ? "_" : "") +
                (isFinalNode ? "*" : "") +
                (isEncodingStartPoint ? ">" : "") +
                (isEncodingEndPoint ? "<" :  "") +
                (label.isBlank() ? "" : " [" + label + "]") +
                (successors.isEmpty() ? "" : " -> ") +
                successors.entrySet().stream()
                        .map(e -> e.getKey().name + (e.getValue().isBlank() ? "" : " [" + e.getValue() + "]"))
                        .collect(Collectors.joining(", "));
    }
}

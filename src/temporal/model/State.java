package temporal.model;

import java.util.*;

public class State {
    private List<String> atoms;
    private String stateName;

    public State(String stateName) {
        this.stateName = stateName;
        this.atoms = new ArrayList<>();
    }

    public String getStateName() { return this.stateName; }

    public void setStateName(String stateName) { this.stateName = stateName; }

    public List<String> getAtoms() { return this.atoms; }

    public void setAtoms(List<String> atoms) { this.atoms = new ArrayList<>(atoms); }

    @Override
    public boolean equals(Object obj) {
        return this.stateName.equals(((State) obj).getStateName());
    }
}

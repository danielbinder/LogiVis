package temporal.model;

public class Transition {

    private String transitionName;
    private State fromState;
    private State toState;

    public Transition(State fromState, State toState) {
        this.fromState = fromState;
        this.toState = toState;
    }

    public Transition(String transitionName, State fromState, State toState) {
        this(fromState, toState);
        this.transitionName = transitionName;
    }

    public String getTransitionName() { return transitionName; }

    public State getFromState() { return fromState; }

    public State getToState() { return toState; }

    @Override
    public boolean equals(Object obj) {
        Transition other = (Transition) obj;
        return this.fromState.equals(other.getFromState()) && this.toState.equals(other.getToState());
    }
}

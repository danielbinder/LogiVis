package bool.variant.cnf.parser.cnfnode;

import bool.variant.cnf.interpreter.decisionGraph.DecisionGraph;
import bool.variant.cnf.interpreter.decisionGraph.DecisionGraphNode;
import bool.variant.cnf.parser.CNFParser;
import marker.ConceptRepresentation;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Conjunction extends ArrayList<Clause> implements ConceptRepresentation {
    @Serial
    private static final long serialVersionUID = -6518674669970157284L;
    public static final Conjunction UNSAT_CONJUNCTION = new Conjunction(List.of(new Clause(List.of())), List.of(), Map.of());
    public final List<Variable> variables;
    public final Map<Variable, Boolean> assignment = new HashMap<>();
    public final Map<Variable, Boolean> assignmentBuffer = new HashMap<>();
    public final DecisionGraph decisionGraph = new DecisionGraph();

    public Conjunction(List<Clause> clauses, List<Variable> variables, Map<Variable, Boolean> assignment) {
        super();

        addAll(clauses);
        this.variables = variables;
        this.assignment.putAll(assignment);
    }

    private Conjunction() {
        super();

        variables = new ArrayList<>();
    }

    public static Conjunction of(String input) {
        return CNFParser.parse(input);
    }

    public boolean containsVariable(Variable variable) {
        return stream().anyMatch(clause -> clause.containsVariable(variable));
    }

    public Conjunction withUnitClause(AbstractVariable variable) {
        List<AbstractVariable> list = new ArrayList<>();
        list.add(variable);
        Clause c = new Clause(list);
        add(0, c);

        return this;
    }

    public void backtrackUIP(AbstractVariable firstUIP) {
        // TODO: this is wrong!!! fix pls this goes back to lastUIP
        DecisionGraphNode node = decisionGraph.removeLast();
        List<Variable> decisionVariables = node.getDecisionVariables();
        decisionVariables.forEach(assignment::remove);

        addAll(node.getConflictClauses());
        stream()
                .filter(clause -> clause.stream()
                        .anyMatch(var -> decisionVariables.contains(var.getVariable())))
                .forEach(clause -> clause.resetWatcherIndices(assignment));
    }

    public Conjunction withRemainingClausesAssignedTrue() {
        forEach(clause -> clause
                .forEach(var -> assignment.put(var.getVariable(), var.isPositive())));

        assignmentBuffer.keySet().stream()
                .filter(var -> !assignment.containsKey(var))
                .forEach(var -> assignment.put(var, assignmentBuffer.get(var)));

        return this;
    }

    public void removeSafe(Clause toRemove) {
        remove(toRemove);

        toRemove.forEach(var -> assignmentBuffer.put(var.getVariable(), var.isPositive()));
    }

    public Conjunction clone() {
        List<Clause> cloned = new ArrayList<>();
        forEach(clause -> cloned.add(clause.clone()));

        return new Conjunction(cloned, variables, new HashMap<>(assignment));
    }

    @Override
    public String toString() {
        return stream()
                .map(Clause::toString)
                .collect(Collectors.joining(" & "));
    }
}

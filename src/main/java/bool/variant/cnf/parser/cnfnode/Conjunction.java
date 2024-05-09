package bool.variant.cnf.parser.cnfnode;

import bool.variant.cnf.interpreter.decisionGraph.DecisionGraph;
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
    public final Map<Variable, List<Clause>> removedClauses = new HashMap<>();
    public final List<AbstractVariable> futureAssignments = new ArrayList<>();

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

    public void backtrack() {
        // TODO csteidl
//        List<Clause> conflictClauses = decisionGraph.getLast()
//                .getConflictClausesStartingFrom(decisionGraph.getFirstAndLastUIP().left);
//
//        Clause conflictClause = decisionGraph.constructConflictClause();
//
//        List<AbstractVariable> conflictParticipants = conflictClauses
//                .stream()
//                .flatMap(Collection::stream)
//                .distinct()
//                .toList();
//
//        DecisionGraphNodeOld backJumpingNode = decisionGraph.getBackJumpingNode(conflictParticipants);
//        List<Variable> backtrackedVariables = new ArrayList<>();
//        DecisionGraphNodeOld current;
//        do {
//            current = decisionGraph.removeLast();
//            current.getDecisionVariables().forEach(assignment::remove);
//            backtrackedVariables.addAll(current.getDecisionVariables());
//        } while(!backJumpingNode.equals(current));
//
//        // restore removed clauses
//        backtrackedVariables.forEach(var -> {
//            addAll(removedClauses.get(var));
//            removedClauses.remove(var);
//        });
//
//        // reset watchers for backtracked variables
//        stream()
//                .filter(clause -> clause.stream()
//                        .anyMatch(var -> backtrackedVariables.contains(var.getVariable())))
//                .forEach(clause -> clause.resetWatcherIndices(assignment));
//
//        add(conflictClause);
    }

    public Conjunction withRemainingClausesAssignedTrue() {
        forEach(clause -> clause.stream()
                .filter(var -> !assignment.containsKey(var.getVariable()))
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

package bool.variant.cnf.parser.cnfnode;

import bool.variant.cnf.parser.CNFParser;
import marker.ConceptRepresentation;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

public class Conjunction extends ArrayList<Clause> implements ConceptRepresentation {
    @Serial
    private static final long serialVersionUID = -6518674669970157284L;
    public static final Conjunction EMPTY_CONJUNCTION = new Conjunction();
    public static final Conjunction UNSAT_CONJUNCTION = new Conjunction(List.of(new Clause(List.of())), List.of(), Map.of());
    public final List<Variable> variables;
    public final Map<Variable, Boolean> assignment = new HashMap<>();
    public final Map<Variable, Set<Clause>> variableReferences = new HashMap<>();

    public Conjunction(List<Clause> clauses, List<Variable> variables, Map<Variable, Boolean> assignment) {
        super();

        addAll(clauses);
        variables.forEach(var -> variableReferences.put(var, new HashSet<>()));
        forEach(clause -> clause
                .forEach(var -> variableReferences.get(var.getVariable()).add(clause)));
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

    public Clause findClause(Clause clause) {
        return stream()
                .filter(c -> c.equals(clause))
                .findFirst()
                .orElse(null);
    }

    public Conjunction addClause(AbstractVariable...variables) {
        Clause c = new Clause(variables);

        add(c);
        c.forEach(variable -> variableReferences.get(variable.getVariable()).add(c));

        return this;
    }

    public Conjunction withUnitClause(AbstractVariable variable) {
        List<AbstractVariable> list = new ArrayList<>();
        list.add(variable);
        Clause c = new Clause(list);
        add(c);
        variableReferences.get(variable.getVariable()).add(c);

        return this;
    }

    public Conjunction withRemainingClausesAssignedTrue() {
        forEach(clause -> clause
                .forEach(var -> assignment.put(var.getVariable(), var.isPositive())));

        return this;
    }

    public Conjunction clone() {
        List<Clause> cloned = new ArrayList<>();
        forEach(clause -> cloned.add(clause.clone()));

        return new Conjunction(cloned, variables, assignment);
    }

    @Override
    public String toString() {
        return stream()
                .map(Clause::toString)
                .collect(Collectors.joining(" & "));
    }
}

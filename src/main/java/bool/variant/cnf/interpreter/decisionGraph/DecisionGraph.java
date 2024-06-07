package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Variable;
import util.Logger;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DecisionGraph extends HashSet<DecisionGraphNode> {
    @Serial
    private static final long serialVersionUID = -6003666467876720212L;

    public int level = 0;

    public DecisionGraph() {

    }

    private DecisionGraph(HashSet<DecisionGraphNode> nodes) {
        addAll(nodes);

        level = nodes.stream()
                .map(node -> node.level)
                .max(Integer::compareTo)
                .orElse(0);
    }

    /**
     * Use this if you have only UNKNOWN clauses
     */
    public void makeDecision(Map<Variable, Boolean> assignments, AbstractVariable decision) {
        assignments.put(decision.getVariable(), decision.isPositive());
        add(new Decision(stream().anyMatch(node -> node.level == 0) ? ++level : 0, decision));
    }

    /**
     * Use this for DECIDABLE clauses
     */
    public void deriveDecision(Map<Variable, Boolean> assignments, Clause toDecide) {
        AbstractVariable decidable = toDecide.getDecidable(assignments);
        assignments.put(decidable.getVariable(), decidable.isPositive());
        add(new ForcedDecision(level, decidable, findDecisions(toDecide, decidable)));
    }

    /**
     * Use this for UNSAT clauses
     */
    public void deriveConflict(Clause conflictClause) {
        add(new Conflict(level, findDecisions(conflictClause)));
    }

    /**
     * Call deriveDecision(...) right after deriveUnitDecision(...), since additional assignments may be forced.
     * Don't query for all status values at the same time, since conjunction may contain (a) & (!a).
     * */
    public void deriveUnitDecision(Map<Variable, Boolean> assignments, AbstractVariable decision) {
        assignments.put(decision.getVariable(), decision.isPositive());
        add(new UnitDecision(decision));
    }

    /**
     * Get the conflict Clause to add for the next run AND
     * Get all variables that have been backtracked
     */
    public Clause constructConflictClause(Map<Variable, Boolean> assignment) {
        Conflict conflict = findConflict();
        Decision firstUIP = getFirstUIP(conflict);
        Logger.info(1, "First UIP: " + firstUIP.decision);
        Clause conflictClause = conflict.getConflictClause();
        Logger.info(2, "Conflict Ancestors:\n" + conflictClause.stream()
                .map(AbstractVariable::negated)
                .map(this::findDecision)
                .filter(x -> x instanceof HasAncestors)
                .map(x -> (HasAncestors) x)
                .map(x -> x.getAllAncestorsOnSameLevel().stream()
                        .map(anc -> anc.decision.toString())
                        .collect(Collectors.joining(", ", "\t\t", "")))
                .collect(Collectors.joining("\n")));
        Logger.info(2, "Unexpanded conflict clause: " + conflictClause);
        List<Decision> expandedConflictParticipants = new ArrayList<>();

        for(Decision decision : conflict.ancestors)
            if(decision instanceof ForcedDecision fd)
                conflictClause = expandConflictClauseToFirstUIP(fd, conflictClause, firstUIP, expandedConflictParticipants);

        Logger.info(1, "Expanded conflict clause: " + conflictClause);

        // minimize
        int oldConflictClauseSize;
        do {
            oldConflictClauseSize = conflictClause.size();
            Clause finalResultingConflictClause = conflictClause;
            conflictClause = new Clause(
                    conflictClause.stream()
                            .filter(var -> !(findDecision(var) instanceof ForcedDecision fd) ||
                                    !finalResultingConflictClause.containsAllVariables(
                                            fd.getReasons().stream()
                                                    .map(AbstractVariable::getVariable)
                                                    .toList()))
                            .toList()
            );
        } while(conflictClause.size() < oldConflictClauseSize);

        // decrease level until conflict clause is satisfiable
        Map<Variable, Boolean> currentAssignment = new HashMap<>(assignment);
        while(level > 0 && conflictClause.getStatus(currentAssignment) == Clause.Status.UNSAT) {
            stream()
                    .filter(node -> node.level == level)
                    .filter(node -> node instanceof Decision)
                    .map(node -> (Decision) node)
                    .filter(decision -> currentAssignment.containsKey(decision.decision.getVariable()))
                    .forEach(decision -> currentAssignment.remove(decision.decision.getVariable()));
            level--;
        }

        return conflictClause;
    }

    public boolean hasConflict() {
        return stream().anyMatch(node -> node instanceof Conflict);
    }

    public DecisionGraph clone() {
        return new DecisionGraph(this);
    }

    private Clause expandConflictClauseToFirstUIP(ForcedDecision decision, Clause conflictClause,
                                                 Decision firstUIP, List<Decision> expandedConflictParticipants) {
        if(decision.level != level || firstUIP.equals(decision) || expandedConflictParticipants.contains(decision))
            return conflictClause;

        expandedConflictParticipants.add(decision);
        conflictClause = conflictClause.resolution(decision.getConflictClause());

        for(Decision ancestor : decision.ancestors)
            if(ancestor instanceof ForcedDecision fd)
                conflictClause = expandConflictClauseToFirstUIP(fd, conflictClause, firstUIP, expandedConflictParticipants);

        return conflictClause;
    }

    // i.e. find first common ancestor on the same level
    private Decision getFirstUIP(Conflict conflict) {
        return conflict.ancestors.size() == 1
                ? conflict.ancestors.stream().findAny().orElseThrow(NoSuchElementException::new)
                : conflict.ancestors.stream()
                .filter(node -> node.level == level)
                .map(this::findAncestorsOnSameLevel)
                .filter(list -> !list.isEmpty())
                .reduce((a, b) -> {
                            List<Decision> result = new ArrayList<>(a);
                            result.removeIf(e -> !b.contains(e));
                            return result;
                })
                // else lastUIP = firstUIP
                .orElse(stream().filter(node -> node.level == level)
                                .filter(node -> !(node instanceof HasAncestors))
                                .map(node -> (Decision) node)
                                .toList())
                .getFirst();
    }

    private List<Decision> findAncestorsOnSameLevel(DecisionGraphNode node) {
        if(node instanceof HasAncestors ha) return ha.getAllAncestorsOnSameLevel();

        return List.of();
    }

    private Decision findDecision(AbstractVariable toFind) {
        return stream()
                .filter(node -> !(node instanceof Conflict))
                .map(node -> (Decision) node)
                .filter(decision -> decision.decision.getVariable().equals(toFind.getVariable()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    private Conflict findConflict() {
        return stream()
                .filter(node -> node instanceof Conflict)
                .map(node -> (Conflict) node)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    private Set<Decision> findDecisions(Clause clause) {
        return stream()
                .filter(node -> !(node instanceof Conflict))
                .map(node -> (Decision) node)
                .filter(decision -> clause.containsVariable(decision.decision.getVariable()))
                .collect(Collectors.toSet());
    }

    private Set<Decision> findDecisions(Clause clause, AbstractVariable exclusion) {
        return stream()
                .filter(node -> !(node instanceof Conflict))
                .map(node -> (Decision) node)
                .filter(decision -> !decision.decision.equals(exclusion))
                .filter(decision -> clause.containsVariable(decision.decision.getVariable()))
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "Decision Graph:\n" +
                IntStream.range(0, level + 1)
                .mapToObj(i -> stream().filter(node -> node.level == i)
                        .sorted()
                        .map(node -> node instanceof Conflict c
                                ? "K" + c.ancestors.stream()
                                        .map(a -> a.decision.toString())
                                        .collect(Collectors.joining(", ", "(", ")"))
                                : ((Decision) node).decision.toString() + (node instanceof HasAncestors ha
                                        ? ha.getDirectAncestors().stream()
                                                .map(a -> a.decision.toString())
                                                .collect(Collectors.joining(", ", "(", ")"))
                                        : ""))
                        .collect(Collectors.joining(" -> ", "\t\t" + i + ": ", "")))
                .collect(Collectors.joining("\n"));
    }
}

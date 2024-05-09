package bool.variant.cnf.interpreter.decisionGraph;

import bool.variant.cnf.parser.cnfnode.AbstractVariable;
import bool.variant.cnf.parser.cnfnode.Clause;
import bool.variant.cnf.parser.cnfnode.Variable;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecisionGraph extends HashSet<DecisionGraphNode> {
    @Serial
    private static final long serialVersionUID = -6003666467876720212L;

    public int level = 0;
    // is calculated in constructConflictClause()
    private int backtrackLevel = 0;

    /**
     * Use this if you have only UNKNOWN clauses
     */
    public void makeDecision(Map<Variable, Boolean> assignments, AbstractVariable decision) {
        add(new Decision(++level, decision));
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
     * Use this if you for UNSAT clauses
     */
    public void deriveConflict(Clause conflictClause) {
        add(new Conflict(level, findDecisions(conflictClause)));
    }

    /**
     * Get the conflict Clause to add for the next run
     */
    public Clause constructConflictClause() {
        Conflict conflict = findConflict();
        Decision lastUIP = getLastUIP(conflict);
        Clause conflictClause = conflict.getConflictClause();
        List<Decision> expandedConflictParticipants = new ArrayList<>();

        for(Decision decision : conflict.ancestors)
            if(decision instanceof ForcedDecision fd)
                conflictClause = expandConflictClauseToLastUIP(fd, conflictClause, lastUIP, expandedConflictParticipants);

        // minimize
        int oldConflictClauseSize;
        do {
            oldConflictClauseSize = conflictClause.size();
            Clause finalResultingConflictClause = conflictClause;
            conflictClause = new Clause(conflictClause.stream()
                                                .filter(var -> findDecision(var) instanceof ForcedDecision fd &&
                                                        !finalResultingConflictClause.containsAll(fd.getReasons()))
                                                .toList());
        } while(conflictClause.size() < oldConflictClauseSize);

        // calculate back jumping level
        backtrackLevel = expandedConflictParticipants
                .stream()
                .flatMap(decision -> decision instanceof ForcedDecision fd
                        ? fd.ancestors.stream()
                        : Stream.of(decision))
                .filter(decision -> decision.level != level)
                .map(decision -> decision.level)
                .max(Integer::compareTo)
                .orElse(0);

        return conflictClause;
    }

    /**
     * Get all variables that have been backtracked
     * CAREFUL! Only call AFTER constructConflictClause(), since this calculates the back jumping level!
     */
    public List<Variable> backtrack() {
        List<Variable> backtrackedVariables = stream()
                .filter(node -> node.level >= backtrackLevel)
                .filter(node -> node instanceof Decision)
                .map(node -> ((Decision) node).decision.getVariable())
                .toList();

        removeIf(node -> node.level >= backtrackLevel);

        return backtrackedVariables;
    }

    private Clause expandConflictClauseToLastUIP(ForcedDecision decision, Clause conflictClause,
                                                 Decision lastUIP, List<Decision> expandedConflictParticipants) {
        if(decision.level != level && !lastUIP.equals(decision) && !expandedConflictParticipants.contains(decision))
            return conflictClause;

        expandedConflictParticipants.add(decision);
        conflictClause.resolution(decision.getConflictClause());

        for(Decision ancestor : decision.ancestors)
            if(ancestor instanceof ForcedDecision fd)
                conflictClause = expandConflictClauseToLastUIP(fd, conflictClause, lastUIP, expandedConflictParticipants);

        return conflictClause;
    }

    // i.e. find first common ancestor
    private Decision getLastUIP(Conflict conflict) {
        return conflict.ancestors.stream()
                .filter(node -> node.level == level)
                .map(this::findAncestorsOnSameLevel)
                .reduce((a, b) -> new ArrayList<>(a.stream().filter(b::contains).toList()))
                .orElse(List.of())
                .stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private List<Decision> findAncestorsOnSameLevel(DecisionGraphNode node) {
        if(node instanceof Decision) return List.of();

        return ((HasAncestors) node).getAllAncestorsOnSameLevel();
    }

    private Decision findDecision(AbstractVariable toFind) {
        return stream()
                .filter(node -> !(node instanceof Conflict))
                .map(node -> (Decision) node)
                .filter(decision -> decision.decision.equals(toFind))
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
                .filter(decision -> clause.contains(decision.decision))
                .collect(Collectors.toSet());
    }

    private Set<Decision> findDecisions(Clause clause, AbstractVariable exclusion) {
        return stream()
                .filter(node -> !(node instanceof Conflict))
                .map(node -> (Decision) node)
                .filter(decision -> !decision.decision.equals(exclusion))
                .filter(decision -> clause.contains(decision.decision))
                .collect(Collectors.toSet());
    }
}

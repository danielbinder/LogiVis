package bool.interpreter;

import bool.parser.logicnode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BruteForceSolver implements BooleanAlgebraSolver {
    private final LogicNode formula;
    private final Map<String, Boolean> currentAssignment = new HashMap<>();
    private final List<String> assignments = new ArrayList<>();     // all possible variable assignments as binary string
    private int assignmentIndex = 0;
    public final List<String> solutionInfo = new ArrayList<>();
    public boolean unsatisfiable = false;
    public boolean valid = false;

    public BruteForceSolver(LogicNode formula) {
        this.formula = formula;
    }

    public BruteForceSolver(String formula) {
        this(LogicNode.of(formula));
    }

    public Map<String, Boolean> solve() {
        initAssignmentMap(formula);
        initAssignmentList();
        assignNext();

        while(!checkCurrentAssignment(formula) && assignmentIndex < assignments.size()) assignNext();

        if(checkCurrentAssignment(formula)) return new HashMap<>(currentAssignment);
        else unsatisfiable = true;

        return Map.of();
    }

    public List<Map<String, Boolean>> solveAll() {
        List<Map<String, Boolean>> satisfiableAssignments = new ArrayList<>();
        initAssignmentMap(formula);
        initAssignmentList();

        while(assignmentIndex < assignments.size()) {
            assignNext();

            if(checkCurrentAssignment(formula)) satisfiableAssignments.add(new HashMap<>(currentAssignment));
        }

        if(satisfiableAssignments.size() == assignments.size()) valid = true;

        return satisfiableAssignments;
    }

    private void initAssignmentMap(LogicNode formula) {
        switch(formula) {
            case ActionNode n -> currentAssignment.put(n.name(), false);
            case NegationNode n -> initAssignmentMap(n.child());
            case AndNode n -> {
                initAssignmentMap(n.left());
                initAssignmentMap(n.right());
            }
            case OrNode n -> {
                initAssignmentMap(n.left());
                initAssignmentMap(n.right());
            }
            case ImplicationNode n -> {
                initAssignmentMap(n.left());
                initAssignmentMap(n.right());
            }
            case DoubleImplicationNode n -> {
                initAssignmentMap(n.left());
                initAssignmentMap(n.right());
            }
            default -> {}
        }
    }

    private void initAssignmentList() {
        for(int i = 0; i < Math.pow(2, currentAssignment.size()); i++) {
            StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(i));

            // Add '0'-padding in front
            while(binaryString.length() < currentAssignment.size()) binaryString.insert(0, "0");

            assignments.add(binaryString.toString());
        }
    }

    private void assignNext() {
        int charIndex = 0;
        char[] assignment = assignments.get(assignmentIndex++).toCharArray();

        for(String varName : currentAssignment.keySet()) currentAssignment.put(varName, assignment[charIndex++] == '1');
        solutionInfo.add("Testing assignment " + currentAssignment.entrySet().stream()
                .map(e -> (e.getValue() ? "" : "!") + e.getKey())
                .collect(Collectors.joining(" ")));
    }

    private boolean checkCurrentAssignment(LogicNode formula) {
        solutionInfo.add("Checking " + formula.toString());
        return switch(formula) {
            case ActionNode n -> currentAssignment.get(n.name());
            case ConstantNode n -> n.bool();
            case NegationNode n -> !checkCurrentAssignment(n.child());
            case AndNode n -> checkCurrentAssignment(n.left()) && checkCurrentAssignment(n.right());
            case OrNode n -> checkCurrentAssignment(n.left()) || checkCurrentAssignment(n.right());
            case ImplicationNode n -> {
                if(checkCurrentAssignment(n.left())) yield checkCurrentAssignment(n.right());

                yield true;
            }
            case DoubleImplicationNode n -> {
                if(checkCurrentAssignment(n.left())) yield checkCurrentAssignment(n.right());
                if(checkCurrentAssignment(n.right())) yield checkCurrentAssignment(n.left());

                yield true;
            }
        };
    }
}

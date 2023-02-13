package interpreter;

import parser.logicnode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BruteForceSolver {
    private final LogicNode formula;
    private final Map<String, Boolean> currentAssignment = new HashMap<>();
    private final List<String> assignments = new ArrayList<>();
    private int assignmentIndex = 0;

    private BruteForceSolver(LogicNode formula) {
        this.formula = formula;
    }

    public static Map<String, String> solve(LogicNode formula) {
        return new BruteForceSolver(formula).solve();
    }

    private Map<String, String> solve() {
        initAssignmentMap(formula);
        initAssignmentList();
        assignNext();

        while(!checkCurrentAssignment(formula)) {
            assignNext();
        }

        return checkCurrentAssignment(formula) ? transformedAssignmentMap() : null;
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
            case FinallyNode n -> initAssignmentMap(n.child());
            case GloballyNode n -> initAssignmentMap(n.child());
            case ImmediateNode n -> initAssignmentMap(n.child());
            case UntilNode n -> {
                initAssignmentMap(n.left());
                initAssignmentMap(n.right());
            }
            default -> {}
        }
    }

    private void initAssignmentList() {
        for(int i = 0; i < currentAssignment.size(); i++) {
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
    }

    private boolean checkCurrentAssignment(LogicNode formula) {
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
            //TODO:
            case ImmediateNode n -> false;
            case FinallyNode n -> false;
            case GloballyNode n -> false;
            case UntilNode n -> false;
        };
    }

    private Map<String, String> transformedAssignmentMap() {
        Map<String, String> assignmentMap = new HashMap<>();

        for(Map.Entry<String, Boolean> e : currentAssignment.entrySet()) assignmentMap.put(e.getKey(), String.valueOf(e.getValue()));

        return assignmentMap;
    }
}

package interpreter;

import parser.logicnode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<Map<String, String>> solveAll(LogicNode formula) {
        return new BruteForceSolver(formula).solveAll();
    }

    public static String resultToJSON(List<Map<String, String>> assignments) {
        if(assignments == null) return "{\"result\":\"unsatisfiable\"}";
        if(assignments.stream().anyMatch(m -> "valid".equals(m.get("result")))) return "{\"result\":\"valid\"}";
        return "{" + assignments.stream()
                .map(a -> "\"assignment_" + assignments.indexOf(a) + "\":" + resultToJSON(a))
                .collect(Collectors.joining(",")) + "}";
    }


    public static String resultToJSON(Map<String, String> map) {
        if(map == null) return "{\"result\":\"unsatisfiable\"}";
        return map.entrySet()
                .stream()
                .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
                .collect(Collectors.joining(",", "{", "}"));
    }

    private Map<String, String> solve() {
        initAssignmentMap(formula);
        initAssignmentList();
        assignNext();

        while(!checkCurrentAssignment(formula) && assignmentIndex < assignments.size()) assignNext();

        return checkCurrentAssignment(formula) ? transformedAssignmentMap() : null;
    }

    private List<Map<String, String>> solveAll() {
        List<Map<String, String>> satisfiableAssignments = new ArrayList<>();
        initAssignmentMap(formula);
        initAssignmentList();

        while(assignmentIndex < assignments.size()) {
            assignNext();

            if(checkCurrentAssignment(formula)) satisfiableAssignments.add(transformedAssignmentMap());
        }

        if(satisfiableAssignments.size() == assignments.size()) return List.of(Map.of("result", "valid"));

        return satisfiableAssignments.size() > 0 ? satisfiableAssignments : null;
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
        };
    }

    private Map<String, String> transformedAssignmentMap() {
        Map<String, String> assignmentMap = new HashMap<>();

        for(Map.Entry<String, Boolean> e : currentAssignment.entrySet()) assignmentMap.put(e.getKey(), String.valueOf(e.getValue()));

        return assignmentMap;
    }
}

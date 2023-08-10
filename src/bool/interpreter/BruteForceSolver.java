package bool.interpreter;

import bool.parser.logicnode.*;
import servlet.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BruteForceSolver {
    private final LogicNode formula;
    private final Map<String, Boolean> currentAssignment = new HashMap<>();
    private final List<String> assignments = new ArrayList<>();     // all possible variable assignments as binary string
    private int assignmentIndex = 0;

    private BruteForceSolver(LogicNode formula) {
        this.formula = formula;
    }

    private BruteForceSolver(String formula) {
        this(LogicNode.of(formula));
    }

    /* I N T E R F A C E */

    public static Result solveAsResult(String formula) {
        return new BruteForceSolver(formula).solveAsResult();
    }

    public static Map<String, Boolean> solve(String formula) {
        return new BruteForceSolver(formula).solve();
    }

    public static Result solveAsResult(LogicNode formula) {
        return new BruteForceSolver(formula).solveAsResult();
    }

    public static Map<String, Boolean> solve(LogicNode formula) {
        return new BruteForceSolver(formula).solve();
    }

    public static Result solveAllAsResult(String formula) {
        return new BruteForceSolver(formula).solveAllAsResult();
    }

    public static Result solveAllAsResult(LogicNode formula) {
        return new BruteForceSolver(formula).solveAllAsResult();
    }

    public static List<Map<String, Boolean>> solveAll(String formula) {
        return new BruteForceSolver(formula).solveAll();
    }

    public static List<Map<String, Boolean>> solveAll(LogicNode formula) {
        return new BruteForceSolver(formula).solveAll();
    }

    /* H E L P E R S */

    private Result solveAsResult() {
        Map<String, Boolean> satisfiableAssignment = solve();

        if(satisfiableAssignment == null) return new Result("unsatisfiable");
        return new Result(satisfiableAssignment);
    }

    private Map<String, Boolean> solve() {
        initAssignmentMap(formula);
        initAssignmentList();
        assignNext();

        while(!checkCurrentAssignment(formula) && assignmentIndex < assignments.size()) assignNext();

        return checkCurrentAssignment(formula) ? new HashMap<>(currentAssignment) : null;
    }

    private Result solveAllAsResult() {
        List<Map<String, Boolean>> satisfiableAssignments = solveAll();

        if(satisfiableAssignments.isEmpty()) return new Result("unsatisfiable");
        if(satisfiableAssignments.size() == assignments.size()) return new Result("valid");
        return new Result(satisfiableAssignments);
    }

    private List<Map<String, Boolean>> solveAll() {
        List<Map<String, Boolean>> satisfiableAssignments = new ArrayList<>();
        initAssignmentMap(formula);
        initAssignmentList();

        while(assignmentIndex < assignments.size()) {
            assignNext();

            if(checkCurrentAssignment(formula)) satisfiableAssignments.add(new HashMap<>(currentAssignment));
        }

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
}

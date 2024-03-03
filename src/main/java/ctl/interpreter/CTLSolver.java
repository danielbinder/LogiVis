package ctl.interpreter;

import ctl.parser.ctlnode.*;
import model.variant.kripke.KripkeNode;
import model.variant.kripke.KripkeStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CTLSolver {
    public final KripkeStructure kripkeStructure;
    public final List<String> solutionInfo = new ArrayList<>();
    public CTLSolver(KripkeStructure kripkeStructure) {
        this.kripkeStructure = kripkeStructure;
    }

    public Map<String, Boolean> solve(CTLNode expression) {
        solutionInfo.clear();
        solutionInfo.add("Checking satisfiability of " + expression.toString() + " for every state");

        List<KripkeNode> states = sat(expression);
        solutionInfo.add("Calculated set of satisfying states by backtracking through the expression hierarchy");

        return kripkeStructure.stream().collect(Collectors.toMap(kn -> kn.name, states::contains));
    }

    private List<KripkeNode> sat(CTLNode expression) {
        return switch(expression) {
            case ConstantNode n when n.bool() -> {
                solutionInfo.add("Taking all states for " + n);
                yield new ArrayList<>(kripkeStructure.stream().toList());
            }
            case ConstantNode n -> {
                solutionInfo.add("Taking no states for " + n);
                yield new ArrayList<>();
            }
            case ActionNode n -> {
                solutionInfo.add("Taking all states that are true for " + n);
                yield new ArrayList<>(kripkeStructure.stream().filter(kn -> kn.stateMap.get(n.name())).toList());
            }
            // xor
            case NegationNode n -> {
                solutionInfo.add("Taking XOR of all states child states of " + n);
                List<KripkeNode> not = sat(n.child());
                yield kripkeStructure.stream().filter(kn -> !not.contains(kn)).toList();
            }
            // intersection
            case AndNode n -> {
                solutionInfo.add("Taking INTERSECTION of satisfying left- and right states of " + n);
                List<KripkeNode> left = sat(n.left());
                yield sat(n.right()).stream().filter(left::contains).toList();
            }
            // union
            case OrNode n -> {
                solutionInfo.add("Taking UNION of satisfying left- and right states of " + n);
                List<KripkeNode> union = new ArrayList<>(sat(n.left()));
                union.addAll(sat(n.right()).stream().filter(node -> !union.contains(node)).toList());
                yield union;
            }
            case ImplicationNode n -> {
                solutionInfo.add("Using DeMorgan to transform 'a -> b' to '!a | b' for " + n);
                yield sat(new OrNode(new NegationNode(n.left()), n.right()));
            }
            case DoubleImplicationNode n -> {
                solutionInfo.add("Splitting up double implication 'a <-> b' to '(a -> b) & (b -> a)' for " + n);
                yield sat(new AndNode(new ImplicationNode(n.left(), n.right()), new ImplicationNode(n.right(), n.left())));
            }
            // evaluate CTL-expression for checking if a certain property holds at least
            // in one path directly in the next state (no simplifications via semantic equivalence possible)
            case EXNode n -> satEX(n.child());
            case AXNode n -> {
                // evaluate CTL-expression for checking if a certain property holds directly in the next states
                // on all outgoing paths
                solutionInfo.add("Transforming 'AX(a)' to '!EX(!a)' for " + n);
                yield new ArrayList<>(sat(new NegationNode(new EXNode(new NegationNode(n.child())))))
                        .stream()
                        .filter(from -> !from.successors.isEmpty())
                        .toList();
            }
            // evaluate CTL-expression for checking if a certain property holds until at some point in the
            // future another property holds on at least one of the outgoing paths
            case EUNode n -> {
                solutionInfo.add("Checking if " + n.left() + " holds until " + n.right());
                yield satEU(n.left(), n.right());
            }
            case AUNode n -> {
                // evaluate CTL-expression for checking if a certain property holds until at some point in the
                // future another property holds on all outgoing paths
                solutionInfo.add("Transforming 'A(a U b)' to '!(E(!b U (!a & !b)) | (EG !b))' for " + n);
                yield sat(new NegationNode(new OrNode(new EUNode(new NegationNode(n.right()),
                                                                 new AndNode(new NegationNode(n.left()),
                                                                             new NegationNode(n.right()))),
                                                      new EGNode(new NegationNode(n.right())))));
            }
            case EFNode n -> {
                // evaluate CTL-expression for checking if a certain property holds eventually on at least
                // one of the outgoing paths
                solutionInfo.add("Transforming 'EF a' to 'E true U a' for " + n);

                yield sat(new EUNode(new ConstantNode(true), n.child()));
            }
            // evaluate CTL-expression for checking if a certain property holds eventually on all
            // outgoing paths
            case AFNode n -> {
                solutionInfo.add("Checking if " + n.child() + " holds for all paths eventually");
                yield satAF(n.child());
            }
            case EGNode n -> {
                // evaluate CTL-expression for checking if a certain property holds on the entire subsequent trace
                // of at least one outgoing path
                solutionInfo.add("Transforming 'EG a' to '!AF !a' for " + n);
                yield sat(new NegationNode(new AFNode(new NegationNode(n.child()))));
            }
            case AGNode n -> {
                // evaluate CTL-expression for checking if a certain property holds on the entire subsequent traces
                // of all outgoing paths
                solutionInfo.add("Transforming 'AG a' to '!EF !a'");
                yield sat(new NegationNode(new EFNode(new NegationNode(n.child()))));
            }
        };
    }

    /**
     * Check if the passed expression holds for the CTL operator EX.
     * */
    private List<KripkeNode> satEX(CTLNode expression) {
        return existentialPredecessors(sat(expression));
    }

    /**
     * Obtain predecessor states for the passed target states fulfilling the
     * following rule: {s elem. of States | exists s': s -> s' and s' elem. of exprStates}.
     * */
    private List<KripkeNode> existentialPredecessors(List<KripkeNode> exprStates) {
        solutionInfo.add("Taking all existential predecessors for " + exprStates.stream()
                .map(node -> node.name).collect(Collectors.joining(", ")));
        return kripkeStructure.stream()
                .filter(from -> exprStates.stream().anyMatch(from.successors::contains))
                .toList();
    }

    /**
     * Obtain universal (all-quantified) predecessor states for the passed target states by calculating
     * the difference between the existential predecessors of the target states and the
     * existential predecessors of all available states minus the passed target states.
     * @see <a href="https://q2a.cs.uni-kl.de/34/how-to-compute-the-universal-predecessor"></a>
     * */
    private List<KripkeNode> universalPredecessors(List<KripkeNode> targetStates) {
        solutionInfo.add("Taking all universal predecessors for " + targetStates.stream()
                .map(node -> node.name).collect(Collectors.joining(", ")));
        List<KripkeNode> targetPredecessors = new ArrayList<>(existentialPredecessors(targetStates));
        List<KripkeNode> diffStates = new ArrayList<>(kripkeStructure);
        for(KripkeNode state : targetStates) {
            diffStates.remove(state);
        }
        List<KripkeNode> diffPredecessors = new ArrayList<>(existentialPredecessors(diffStates));
        for(KripkeNode state : diffPredecessors) {
            targetPredecessors.remove(state);
        }
        return targetPredecessors;
    }

    /**
     * Check if the passed expressions hold for the CTL operator EU.
     * */
    private List<KripkeNode> satEU(CTLNode leftExpr, CTLNode rightExpr) {
        List<KripkeNode> leftExprStates = new ArrayList<>(sat(leftExpr));
        List<KripkeNode> rightExprStates = new ArrayList<>(sat(rightExpr));
        List<KripkeNode> temp = new ArrayList<>(kripkeStructure);

        while(!listsEqual(temp, rightExprStates)) {
            temp = rightExprStates;
            List<KripkeNode> exprStates = existentialPredecessors(rightExprStates);
            List<KripkeNode> newRightExprStates = new ArrayList<>(rightExprStates);

            List<KripkeNode> combined = new ArrayList<>();
            for(KripkeNode state : leftExprStates) {
                if(exprStates.contains(state)) {
                    combined.add(state);
                }
            }

            for(KripkeNode state : combined) {
                if(!newRightExprStates.contains(state)) {
                    newRightExprStates.add(state);
                }
            }

            rightExprStates = newRightExprStates;
        }
        return rightExprStates;
    }

    /**
     * Check if the passed expression holds for the CTL operator AF.
     * */
    private List<KripkeNode> satAF(CTLNode expression) {
        List<KripkeNode> exprStates = new ArrayList<>(sat(expression));
        List<KripkeNode> temp = new ArrayList<>(kripkeStructure);

        while(!listsEqual(temp, exprStates)) {
            temp = exprStates;
            List<KripkeNode> newExprStates = new ArrayList<>(exprStates);
            List<KripkeNode> allPredecessors = new ArrayList<>(universalPredecessors(exprStates));
            for(KripkeNode state : allPredecessors) {
                if(!newExprStates.contains(state)) {
                    newExprStates.add(state);
                }
            }
            exprStates = newExprStates;
        }
        return exprStates;
    }

    /**
     * Check if the passed lists contain the same set of states.
     * */
    private boolean listsEqual(List<KripkeNode> one, List<KripkeNode> two) {
        if(one.size() != two.size()) return false;
        for(KripkeNode s : one) {
            if(!two.contains(s)) return false;
        }
        return true;
    }
}

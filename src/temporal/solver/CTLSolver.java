package temporal.solver;

import bool.interpreter.Simplification;
import lexer.Lexer;
import bool.parser.BooleanParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import servlet.Result;
import temporal.model.KripkeStruct;
import temporal.model.State;
import temporal.model.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class CTLSolver {

    /** This kripke structure will be analyzed in order to evaluate whether the
     * properties (i.e., atoms) of individual nodes satisfy the given CTL expression. */
    private final KripkeStruct kripkeStructure;

    /** StringBuilder used to log conducted solving steps. Will be reset when a new
     * CTL expression is to be tested with the same CTLSolver instance. */
    private final StringBuilder solverSteps;

    public CTLSolver(KripkeStruct kripkeStructure) {
        this.kripkeStructure = kripkeStructure;
        this.solverSteps = new StringBuilder();
    }

    public Result getSatisfyingStatesAsResult(String expression) {
        CTLSolverResult solverResult = getSatisfyingStates(expression);
        if(solverResult.isValid()) return new Result(solverResult.getSolverResult(), solverResult.getSolverSteps());
        else return new Result(solverResult.getErrorMessage(), solverResult.getSolverSteps());
    }

    public CTLSolverResult getSatisfyingStates(String expression) {
        try {
            // reset recorded solver steps
            resetSolverSteps();

            // attempt simplification of passed expression
            expression = simplifyExpression(expression);

            // log first solver step
            logStep("Check satisfiability of expression %s for every model state", expression);

            // obtain states satisfying passed expression
            List<State> states = sat(expression.replace(" ", ""));

            // log solving hint
            logStep("Calculated set of satisfying states by going bottom-up in the expression hierarchy");

            // return ordered mapping of state names to a boolean value indicating if associated
            // state satisfies the passed expression
            Map<String, Boolean> result = kripkeStructure.getStates()
                    .stream()
                    .collect(Collectors.toMap(
                            State::getStateName,
                            states::contains
                    ));
            return new CTLSolverResult(new TreeMap<>(result), getSolverSteps());
        } catch(Exception ex) {
            String exMessage = ex.getMessage();
            StackTraceElement ste = ex.getStackTrace()[0];
            if(!ste.getClassName().equals(this.getClass().getName())) {
                exMessage = "invalid expression";
            }
            return new CTLSolverResult(getSolverSteps(), exMessage);
        }
    }

    private BinarySymbol getExprType(String expression) {
        // remove outermost parentheses
        expression = removeFirstSetOfPars(expression);

        // look for binary symbols
        if(expression.contains(SolverConstants.SymAnd)) {
            BinarySymbol sym = isBinaryOperation(expression, SolverConstants.SymAnd);
            if(sym != null && sym.isBinary()) {
                sym.setExprType(ExprType.And);
                return sym;
            }
        }
        if(expression.contains(SolverConstants.SymOr)) {
            BinarySymbol sym = isBinaryOperation(expression, SolverConstants.SymOr);
            if(sym != null && sym.isBinary()) {
                sym.setExprType(ExprType.Or);
                return sym;
            }
        }
        if(expression.contains(SolverConstants.SymImplication) && !expression.contains(SolverConstants.SymDoubleImplication)) {
            BinarySymbol sym = isBinaryOperation(expression, SolverConstants.SymImplication);
            if (sym != null && sym.isBinary()) {
                sym.setExprType(ExprType.Implication);
                return sym;
            }
        }
        if(expression.contains(SolverConstants.SymDoubleImplication)) {
            BinarySymbol sym = isBinaryOperation(expression, SolverConstants.SymDoubleImplication);
            if(sym != null && sym.isBinary()) {
                sym.setExprType(ExprType.DoubleImplication);
                return sym;
            }
        }
        if(expression.startsWith(SolverConstants.SymAUStart)) {
            String stripped = expression.substring(2, expression.length() - 1);
            BinarySymbol sym = isBinaryOperation(stripped, SolverConstants.SymUntil);
            if(sym != null && sym.isBinary()) {
                sym.setExprType(ExprType.AU);
                return sym;
            }
        }
        if(expression.startsWith(SolverConstants.SymEUStart)) {
            String stripped = expression.substring(2, expression.length() - 1);
            BinarySymbol sym = isBinaryOperation(stripped, SolverConstants.SymUntil);
            if(sym != null && sym.isBinary()) {
                sym.setExprType(ExprType.EU);
                return sym;
            }
        }

        // look for unary symbols
        if(expression.equals(SolverConstants.SymTrue)) {
            return new BinarySymbol(false, expression, "", ExprType.True);
        }
        if(expression.equals(SolverConstants.SymFalse)) {
            return new BinarySymbol(false, expression, "", ExprType.False);
        }
        if(isAtomic(expression)) {
            return new BinarySymbol(false, expression, "", ExprType.Atomic);
        }
        if(expression.startsWith(SolverConstants.SymNot)) {
            String leftExpr = expression.substring(1);
            return new BinarySymbol(false, leftExpr, "", ExprType.Not);
        }
        if(expression.startsWith(SolverConstants.SymEX)) {
            String leftExpr = expression.substring(2);
            return new BinarySymbol(false, leftExpr, "", ExprType.EX);
        }
        if(expression.startsWith(SolverConstants.SymAX)) {
            String leftExpr = expression.substring(2);
            return new BinarySymbol(false, leftExpr, "", ExprType.AX);
        }
        if(expression.startsWith(SolverConstants.SymEF)) {
            String leftExpr = expression.substring(2);
            return new BinarySymbol(false, leftExpr, "", ExprType.EF);
        }
        if(expression.startsWith(SolverConstants.SymAF)) {
            String leftExpr = expression.substring(2);
            return new BinarySymbol(false, leftExpr, "", ExprType.AF);
        }
        if(expression.startsWith(SolverConstants.SymEG)) {
            String leftExpr = expression.substring(2);
            return new BinarySymbol(false, leftExpr, "", ExprType.EG);
        }
        if(expression.startsWith(SolverConstants.SymAG)) {
            String leftExpr = expression.substring(2);
            return new BinarySymbol(false, leftExpr, "", ExprType.AG);
        }

        // no known symbol found
        return null;
    }

    private List<State> sat(String expression) throws RuntimeException {
        List<State> states = new ArrayList<>();
        BinarySymbol sym = getExprType(expression);

        if(sym == null) {
            throw new RuntimeException(String.format("Unknown expression/atom/constant '%s'.", expression));
        }

        // log current solver step
        logSolverStep(expression, sym);

        return switch (sym.getExprType()) {
            case True -> {
                states.addAll(kripkeStructure.getStates());
                yield states;
            }
            case False -> states;
            case Atomic -> {
                kripkeStructure.getStates().forEach(state -> {
                    if(state.getAtoms().contains(sym.getLeftExpression())) {
                        states.add(state);
                    }
                });
                yield states;
            }
            case Not -> {
                // calculate exclusive-or between all possible states
                // and states satisfying the left expression
                states.addAll(kripkeStructure.getStates());
                List<State> exprStates = sat(sym.getLeftExpression());
                exprStates.forEach(state -> states.remove(state));
                yield states;
            }
            case And -> {
                // calculate intersection between states satisfying the left expression
                // and states satisfying the right expression
                List<State> leftStates = sat(sym.getLeftExpression());
                List<State> rightStates = sat(sym.getRightExpression());
                leftStates.forEach(state -> {
                    if(rightStates.contains(state)) {
                        states.add(state);
                    }
                });
                yield states;
            }
            case Or -> {
                // calculate union between states satisfying the left expression
                // and states satisfying the right expression
                List<State> leftStates = sat(sym.getLeftExpression());
                List<State> rightStates = sat(sym.getRightExpression());
                states.addAll(leftStates);
                rightStates.forEach(state -> {
                    if(!states.contains(state)) {
                        states.add(state);
                    }
                });
                yield states;
            }
            case Implication -> {
                // apply De Morgan's law to expression ((a -> b) = (!a | b))
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getLeftExpression());
                equivalentExpr.append(SolverConstants.SymOr);
                equivalentExpr.append(sym.getRightExpression());

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                yield sat(equivalentExpr.toString());
            }
            case DoubleImplication -> {
                // split double implication in two conjugated single implications
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymLeftPar);
                equivalentExpr.append(SolverConstants.SymLeftPar);
                equivalentExpr.append(sym.getLeftExpression());
                equivalentExpr.append(SolverConstants.SymImplication);
                equivalentExpr.append(sym.getRightExpression());
                equivalentExpr.append(SolverConstants.SymRightPar);
                equivalentExpr.append(SolverConstants.SymAnd);
                equivalentExpr.append(SolverConstants.SymLeftPar);
                equivalentExpr.append(sym.getRightExpression());
                equivalentExpr.append(SolverConstants.SymImplication);
                equivalentExpr.append(sym.getLeftExpression());
                equivalentExpr.append(SolverConstants.SymRightPar);
                equivalentExpr.append(SolverConstants.SymRightPar);

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                yield sat(equivalentExpr.toString());
            }
            case EX -> {
                // evaluate CTL-expression for checking if a certain property holds at least
                // in one path directly in the next state (no simplifications via semantic equivalence possible)
                String leftExpr = sym.getLeftExpression();
                yield satEX(leftExpr);
            }
            case AX -> {
                // evaluate CTL-expression for checking if a certain property holds directly in the next states
                // on all outgoing paths

                // build semantically equivalent expression
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(SolverConstants.SymEX);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getLeftExpression());

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                List<State> exprStates = new ArrayList<>(sat(equivalentExpr.toString()));
                List<State> realStates = new ArrayList<>();
                for(State from : exprStates) {
                    for(Transition transition : kripkeStructure.getTransitions()) {
                        if(from.equals(transition.getFromState())) {
                            realStates.add(from);
                            break;
                        }
                    }
                }
                states.addAll(realStates);
                yield states;
            }
            case EU -> {
                // evaluate CTL-expression for checking if a certain property holds until at some point in the
                // future another property holds on at least one of the outgoing paths
                String leftExpr = sym.getLeftExpression();
                String rightExpr = sym.getRightExpression();
                yield satEU(leftExpr, rightExpr);
            }
            case AU -> {
                // evaluate CTL-expression for checking if a certain property holds until at some point in the
                // future another property holds on all outgoing paths

                // build semantically equivalent expression
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(SolverConstants.SymLeftPar);
                equivalentExpr.append(SolverConstants.SymEUStart);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getRightExpression());
                equivalentExpr.append(SolverConstants.SymUntil);
                equivalentExpr.append(SolverConstants.SymLeftPar);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getLeftExpression());
                equivalentExpr.append(SolverConstants.SymAnd);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getRightExpression());
                equivalentExpr.append(SolverConstants.SymRightPar);
                equivalentExpr.append(SolverConstants.SymRightPar);
                equivalentExpr.append(SolverConstants.SymOr);
                equivalentExpr.append(SolverConstants.SymLeftPar);
                equivalentExpr.append(SolverConstants.SymEG);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getRightExpression());
                equivalentExpr.append(SolverConstants.SymRightPar);
                equivalentExpr.append(SolverConstants.SymRightPar);

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                yield sat(equivalentExpr.toString());
            }
            case EF -> {
                // evaluate CTL-expression for checking if a certain property holds eventually on at least
                // one of the outgoing paths

                // build semantically equivalent expression
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymEUStart);
                equivalentExpr.append(SolverConstants.SymTrue);
                equivalentExpr.append(SolverConstants.SymUntil);
                equivalentExpr.append(sym.getLeftExpression());
                equivalentExpr.append(SolverConstants.SymRightPar);

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                yield sat(equivalentExpr.toString());
            }
            case AF -> {
                // evaluate CTL-expression for checking if a certain property holds eventually on all
                // outgoing paths
                String leftExpr = sym.getLeftExpression();
                yield satAF(leftExpr);
            }
            case EG -> {
                // evaluate CTL-expression for checking if a certain property holds on the entire subsequent trace
                // of at least one outgoing path

                // build semantically equivalent expression
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(SolverConstants.SymAF);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getLeftExpression());

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                yield sat(equivalentExpr.toString());
            }
            case AG -> {
                // evaluate CTL-expression for checking if a certain property holds on the entire subsequent traces
                // of all outgoing paths

                // build semantically equivalent expression
                StringBuilder equivalentExpr = new StringBuilder();
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(SolverConstants.SymEF);
                equivalentExpr.append(SolverConstants.SymNot);
                equivalentExpr.append(sym.getLeftExpression());

                // log transformation
                logExpressionTransformation(expression, equivalentExpr.toString());

                yield sat(equivalentExpr.toString());
            }
        };
    }

    /**
     * Check if the passed expression holds for the CTL operator EX.
     * */
    private List<State> satEX(String expression) {
        List<State> exprStates = sat(expression);
        return existentialPredecessors(exprStates);
    }

    /**
     * Obtain predecessor states for the passed target states fulfilling the
     * following rule: {s elem. of States | exists s': s -> s' and s' elem. of exprStates}.
     * */
    private List<State> existentialPredecessors(List<State> exprStates) {
        List<State> states = new ArrayList<>();
        for(State from : kripkeStructure.getStates()) {
            for(State to : exprStates) {
                Transition transition = new Transition(from, to);
                if(kripkeStructure.getTransitions().contains(transition) && !states.contains(from)) {
                    states.add(from);
                }
            }
        }
        return states;
    }

    /**
     * Obtain universal (all-quantified) predecessor states for the passed target states by calculating
     * the difference between the existential predecessors of the target states and the
     * existential predecessors of all available states minus the passed target states.
     * @see <a href="https://q2a.cs.uni-kl.de/34/how-to-compute-the-universal-predecessor"></a>
     * */
    private List<State> universalPredecessors(List<State> targetStates) {
        List<State> targetPredecessors = new ArrayList<>(existentialPredecessors(targetStates));
        List<State> diffStates = new ArrayList<>(kripkeStructure.getStates());
        for(State state : targetStates) {
            diffStates.remove(state);
        }
        List<State> diffPredecessors = new ArrayList<>(existentialPredecessors(diffStates));
        for(State state : diffPredecessors) {
            targetPredecessors.remove(state);
        }
        return targetPredecessors;
    }

    /**
     * Check if the passed expressions hold for the CTL operator EU.
     * */
    private List<State> satEU(String leftExpr, String rightExpr) {
        List<State> leftExprStates = new ArrayList<>(sat(leftExpr));
        List<State> rightExprStates = new ArrayList<>(sat(rightExpr));
        List<State> temp = new ArrayList<>(kripkeStructure.getStates());

        while(!listsEqual(temp, rightExprStates)) {
            temp = rightExprStates;
            List<State> exprStates = existentialPredecessors(rightExprStates);
            List<State> newRightExprStates = new ArrayList<>(rightExprStates);

            List<State> combined = new ArrayList<>();
            for(State state : leftExprStates) {
                if(exprStates.contains(state)) {
                    combined.add(state);
                }
            }

            for(State state : combined) {
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
    private List<State> satAF(String expression) {
        List<State> exprStates = new ArrayList<>(sat(expression));
        List<State> temp = new ArrayList<>(kripkeStructure.getStates());

        while(!listsEqual(temp, exprStates)) {
            temp = exprStates;
            List<State> newExprStates = new ArrayList<>(exprStates);
            List<State> allPredecessors = new ArrayList<>(universalPredecessors(exprStates));
            for(State state : allPredecessors) {
                if(!newExprStates.contains(state)) {
                    newExprStates.add(state);
                }
            }
            exprStates = newExprStates;
        }
        return exprStates;
    }

    /**
     * Check if the passed expression is atomic.
     * Example: If a state has an atom 'p', an atomic expression would simply be 'p' too.
     * */
    private boolean isAtomic(String expression) {
        return kripkeStructure.getAtoms().contains(expression);
    }

    /**
     * Check if the passed lists contain the same set of states.
     * */
    private boolean listsEqual(List<State> one, List<State> two) {
        if(one.size() != two.size()) return false;
        for(State s : one) {
            if(!two.contains(s)) return false;
        }
        return true;
    }

    /**
     * Check if the passed expression is a binary operation like AND, OR, IMPLIES, etc.
     * The passed symbol is used for splitting the original expression into its respective parts
     * (left expression, right expression) if the modelled operation is indeed binary.
     * */
    private BinarySymbol isBinaryOperation(String expression, String symbol) {
        int symLength = symbol.length();
        boolean isBinary = false;
        String leftExpr = null, rightExpr = null;
        if(expression.contains(symbol)) {
            int openParCnt = 0, closeParCnt = 0;
            for(int i = 0; i < expression.length(); i++) {
                String currentSym = expression.substring(i, i + 1);
                if((i + symLength) <= expression.length()) {
                    currentSym = expression.substring(i, i + symLength);
                }
                if(currentSym.equals(symbol) && openParCnt == closeParCnt) {
                    leftExpr = expression.substring(0, i);
                    rightExpr = expression.substring(i + symLength, expression.length());
                    isBinary = true;
                    break;
                } else if(SolverConstants.SymLeftPar.equals(Character.toString(expression.charAt(i)))) {
                    ++openParCnt;
                } else if(SolverConstants.SymRightPar.equals(Character.toString(expression.charAt(i)))) {
                    ++closeParCnt;
                }
            }
        }
        return isBinary ? new BinarySymbol(true, leftExpr, rightExpr) : null;
    }

    /**
     * Simply remove enclosing parentheses if present.
     * */
    private String removeFirstSetOfPars(String expression) {
        if(expression.startsWith(SolverConstants.SymLeftPar) && expression.endsWith(SolverConstants.SymRightPar)) {
            return expression.substring(1, expression.length() - 1);
        }
        return expression;
    }

    /**
     * If the passed expression does not contain any temporal symbols, it can be trivially simplified.
     * */
    private String simplifyExpression(String expression) {
        if(SolverConstants.TEMPORAL_SYMBOLS.stream().noneMatch(expression::contains)) {
            String simplified = Simplification.of(new BooleanParser().parse(Lexer.tokenizeBooleanFormula(expression))).toString();
            logStep("Simplified expression %s to %s", expression, simplified);
            return simplified;
        }
        return expression;
    }

    private void resetSolverSteps() {
        this.solverSteps.setLength(0);
    }

    private void logStep(String message, String... params) {
        if(!message.isEmpty()) {
            if(params.length != 0) {
                message = String.format(message, params);
            }
            this.solverSteps.append(message);
            this.solverSteps.append("\n");
        }
    }

    private void logSolverStep(String expression, BinarySymbol sym) {
        switch (sym.getExprType()) {
            case Atomic -> logStep("Check in which states atom %s is present", expression);
            case True -> logStep("All states satisfy constant true (current expression)");
            case False -> logStep("No states satisfy constant false (current expression)");
            default -> {
                logStep("Check satisfiability of "
                    + (sym.isBinary() ? "binary " : "unary ")
                    + "expression %s (outermost expression type " + sym.getExprType().toString() + ") by checking nested "
                    + (sym.isBinary() ? "expressions " : "expression ") + "%s"
                    + (sym.isBinary() ? " and %s" : "")
                    + " first", expression, sym.getLeftExpression(), sym.getRightExpression());
            }
        }
    }

    private void logExpressionTransformation(String originalExpr, String newExpr) {
        logStep("Transformed expression %s to %s by leveraging semantic equivalence.", originalExpr, newExpr);
    }

    private String getSolverSteps() { return this.solverSteps.toString(); }

    @Getter
    @AllArgsConstructor
    public class CTLSolverResult {
        private Map<String, Boolean> solverResult;
        private String solverSteps;
        private boolean valid;
        private String errorMessage;

        public CTLSolverResult(Map<String, Boolean> solverResult, String solverSteps) {
            this(solverResult, solverSteps, true, null);
        }

        public CTLSolverResult(String solverSteps, String errorMessage) {
            this(null, solverSteps, false, errorMessage);
        }
    }
}

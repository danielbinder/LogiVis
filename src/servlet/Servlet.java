package servlet;

import bool.BooleanGenerator;
import bool.interpreter.Parenthesiser;
import bool.parser.logicnode.LogicNode;
import marker.RestEndpoint;
import model.finite.FiniteAutomatonGenerator;
import model.parser.Model;
import model.interpreter.ModelTracer;
import model.kripke.KripkeGenerator;
import bool.interpreter.BruteForceSolver;
import bool.interpreter.Simplification;
import model.kripke.KripkeTruthTable;
import servlet.rest.GET;
import servlet.rest.REST;
import temporal.solver.CTLSolver;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class Servlet implements RestEndpoint {
    private static final int SERVLET_PORT = 4000;
    private static final int APP_PORT = 3000;

    public static void main(String[] args) {
        if(args.length > 0 && args[0].equals("DEV")) Result.DEV = true;

        REST.start(SERVLET_PORT);
        System.out.println("Server started; go to: http://localhost:" + APP_PORT);
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return new Result(() -> new BruteForceSolver(preprocess(formula)),
                          solver -> List.of(solver.solve()),
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GET("/solveAll/:formula")
    public String solveAll(String formula) {
        return new Result(() -> new BruteForceSolver(preprocess(formula)),
                          BruteForceSolver::solveAll,
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable",
                                 solver -> solver.valid, "valid"))
                .computeJSON();
    }

    @GET("/parenthesise/:formula")
    public String parenthesise(String formula) {
        return new Result(() -> Parenthesiser.addNecessaryParenthesis(LogicNode.of(preprocess(formula))))
                .computeJSON();
    }

    @GET("/parenthesiseAll/:formula")
    public String parenthesiseAll(String formula) {
        return new Result(() -> LogicNode.of(preprocess(formula)).toString())
                .computeJSON();
    }

    @GET("/trace/:kripke")
    public String trace(String kripke) {
        return new Result(() -> Model.of(preprocess(kripke)).toModelTracer(),
                          ModelTracer::trace,
                          tracer -> String.join("\n", tracer.solutionInfo))
                .computeJSON();
    }

    @GET("/shortestTrace/:kripke")
    public String shortestTrace(String kripke) {
        return new Result(() -> Model.of(preprocess(kripke)).toModelTracer(),
                          ModelTracer::shortestTrace,
                          tracer -> String.join("\n", tracer.solutionInfo))
                .computeJSON();
    }

    @GET("/solveCTL/:formula/:model")
    public String solveCTL(String formula, String model) {
        return new CTLSolver(Model.of(preprocess(model))
                                     .toKripkeStructure()
                                     .toOtherKripke()).getSatisfyingStatesAsResult(preprocess(formula)).computeJSON();
    }

    @GET("/generateKripke/:nodes/:initialNodes/:variables/:minSuccessors/:maxSuccessors/:allReachable")
    public String generateKripke(String allReachable, String initialNodes,
                           String maxSuccessors, String minSuccessors,
                           String nodes, String variables) {
        return new Result(() -> Model.of(KripkeGenerator.generate(Integer.parseInt(nodes),
                                                                  Integer.parseInt(initialNodes),
                                                                  Integer.parseInt(variables),
                                                                  Integer.parseInt(minSuccessors),
                                                                  Integer.parseInt(maxSuccessors),
                                                                  Boolean.parseBoolean(allReachable)))
                .toModelString())
                .computeJSON();
    }

    @GET("/generateFiniteAutomaton/:nodes/:initialNodes/:finalNodes/:alphabetSize/:minSuccessors/:maxSuccessors/:allReachable")
    public String generateFiniteAutomaton(String allReachable, String alphabetSize, String finalNodes,
                                          String initialNodes, String maxSuccessors, String minSuccessors,
                                          String nodes) {
        return new Result(() -> FiniteAutomatonGenerator.generate(Integer.parseInt(nodes),
                                                                           Integer.parseInt(initialNodes),
                                                                           Integer.parseInt(finalNodes),
                                                                           Integer.parseInt(alphabetSize),
                                                                           Integer.parseInt(minSuccessors),
                                                                           Integer.parseInt(maxSuccessors),
                                                                           Boolean.parseBoolean(allReachable))
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/generateFormula/:variables/:operators")
    public String generateFormula(String operators, String variables) {
        return new Result(() -> BooleanGenerator.generate(variables, operators))
                .computeJSON();
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        return new Result(() -> Model.of(preprocess(kripke))
                .toKripkeStructure()
                .toFormulaString(Integer.parseInt(steps)))
                .computeJSON();
    }

    @GET("/kripke2compactFormula/:kripke/:steps")
    public String kripke2CompactFormula(String kripke, String steps) {
        return new Result(() -> Model.of(preprocess(kripke))
                                 .toKripkeStructure()
                                 .toKripkeTruthTable(),
                         ktt -> ktt.toFormulaStringWithEncodingStartAndEnd(Integer.parseInt(steps)),
                         KripkeTruthTable::toString)
                .computeJSON();
    }

    @GET("/kripke2compactQBFFormula/:kripke/:steps")
    public String kripke2QBFFormula(String kripke, String steps) {
        return new Result(() -> Model.of(preprocess(kripke))
                                 .toKripkeStructure()
                                 .toKripkeTruthTable(),
                         ktt -> ktt.toQBFString(Integer.parseInt(steps)),
                         KripkeTruthTable::toString)
                .computeJSON();
    }

    @GET("/simplify/:formula")
    public String simplify(String formula) {
        return new Result(() -> Simplification.of(preprocess(formula)).toString())
                .computeJSON();
    }

    private String preprocess(String raw) {
        return raw.replaceAll(Matcher.quoteReplacement("$"), "\n");
    }
}

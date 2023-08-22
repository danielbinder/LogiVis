package servlet;

import bool.interpreter.Parenthesiser;
import bool.parser.logicnode.LogicNode;
import model.Model;
import model.ModelTracer;
import model.kripke.KripkeGenerator;
import bool.interpreter.BruteForceSolver;
import bool.interpreter.Simplification;
import model.kripke.KripkeTruthTable;
import servlet.rest.GET;
import servlet.rest.REST;
import temporal.solver.CTLSolver;

import java.util.List;
import java.util.Map;

public class Servlet {
    private static final int SERVLET_PORT = 4000;
    private static final int APP_PORT = 3000;

    public static void main(String[] args) {
        REST.start(SERVLET_PORT);
        System.out.println("Server started; go to: http://localhost:" + APP_PORT);
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return new Result(() -> new BruteForceSolver(formula),
                          solver -> List.of(solver.solve()),
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GET("/solveAll/:formula")
    public String solveAll(String formula) {
        return new Result(() -> new BruteForceSolver(formula),
                          BruteForceSolver::solveAll,
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable",
                                 solver -> solver.valid, "valid"))
                .computeJSON();
    }

    @GET("/parenthesise/:formula")
    public String parenthesise(String formula) {
        return new Result(() -> Parenthesiser.addNecessaryParenthesis(LogicNode.of(formula)))
                .computeJSON();
    }

    @GET("/parenthesiseAll/:formula")
    public String parenthesiseAll(String formula) {
        return new Result(() -> LogicNode.of(formula).toString())
                .computeJSON();
    }

    @GET("/trace/:kripke")
    public String trace(String kripke) {
        return new Result(() -> Model.of(kripke).toModelTracer(),
                          ModelTracer::trace,
                          tracer -> String.join("\n", tracer.solutionInfo))
                .computeJSON();
    }

    @GET("/shortestTrace/:kripke")
    public String shortestTrace(String kripke) {
        return new Result(() -> Model.of(kripke).toModelTracer(),
                          ModelTracer::shortestTrace,
                          tracer -> String.join("\n", tracer.solutionInfo))
                .computeJSON();
    }

    @GET("/solveCTL/:formula/:model")
    public String solveCTL(String formula, String model) {
        return new CTLSolver(Model.of(model)
                                     .toKripkeStructure()
                                     .toOtherKripke()).getSatisfyingStatesAsResult(formula).computeJSON();
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return new Result(() -> Model.of(KripkeGenerator.generate(params, 10))
                .toModelString())
                .computeJSON();
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        return new Result(() -> Model.of(kripke)
                .toKripkeStructure()
                .toFormulaString(Integer.parseInt(steps)))
                .computeJSON();
    }

    @GET("/kripke2compactFormula/:kripke/:steps")
    public String kripke2CompactFormula(String kripke, String steps) {
        return new Result(() -> Model.of(kripke)
                                 .toKripkeStructure()
                                 .toKripkeTruthTable(),
                         ktt -> ktt.toFormulaStringWithEncodingStartAndEnd(Integer.parseInt(steps)),
                         KripkeTruthTable::toString)
                .computeJSON();
    }

    @GET("/kripke2compactQBFFormula/:kripke/:steps")
    public String kripke2QBFFormula(String kripke, String steps) {
        return new Result(() -> Model.of(kripke)
                                 .toKripkeStructure()
                                 .toKripkeTruthTable(),
                         ktt -> ktt.toQBFString(Integer.parseInt(steps)),
                         KripkeTruthTable::toString)
                .computeJSON();
    }

    @GET("/simplify/:formula")
    public String simplify(String formula) {
        return new Result(() -> Simplification.of(formula).toString())
                .computeJSON();
    }
}

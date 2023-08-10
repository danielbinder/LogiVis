package servlet;

import model.Model;
import model.kripke.KripkeGenerator;
import bool.interpreter.BruteForceSolver;
import bool.interpreter.Simplification;
import model.kripke.KripkeStructure;
import servlet.rest.GET;
import servlet.rest.REST;
import temporal.model.KripkeStruct;
import temporal.solver.CTLSolver;

public class Servlet {
    private static final int SERVLET_PORT = 4000;
    private static final int APP_PORT = 3000;

    public static void main(String[] args) {
        REST.start(SERVLET_PORT);
        System.out.println("Server started; go to: http://localhost:" + APP_PORT);
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return BruteForceSolver.solveAsResult(formula)
                .computeJSON();
    }

    @GET("/encodeAndSolveWithTrace/:kripke/:steps")
    public String encodeAndSolveWithTrace(String kripke, String steps) {
        KripkeStructure ks = Model.of(kripke)
                .toKripkeStructure();
        Result formulaResult = ks.toKripkeTruthTable()
                .toFormulaStringWithEncodingStartAndEndAsResult(Integer.parseInt(steps));
        if(ks.stream().noneMatch(kn -> kn.isEncodingStart) || ks.stream().noneMatch(kn -> kn.isEncodingEnd))
            return formulaResult.info("").error("Define encoding start- and endpoint with state suffixes '>' and '<'!").computeJSON();
        Result solverResult = BruteForceSolver.solveAsResult(formulaResult.result);
        if(solverResult.result.equals("unsatisfiable"))
            return formulaResult.info("unsatisfiable").computeJSON();

        return formulaResult
                .info(KripkeStructure.resolveStateTraceMap(Integer.parseInt(steps), ks.getStateTraceMap(), solverResult.result))
                .computeJSON();
    }

    @GET("/solveCTL/:formula/:model")
    public String solveCTL(String formula, String model) {
        String formattedModel = model.replace("_", ";");
        KripkeStruct kripkeStructure = new KripkeStruct(formattedModel);
        return new CTLSolver(kripkeStructure).getSatisfyingStatesAsResult(formula).computeJSON();
    }

    @GET("/solveAll/:formula")
    public String solveAll(String formula) {
        return BruteForceSolver.solveAllAsResult(formula)
                .computeJSON();
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return Model.of(KripkeGenerator.generate(params, 10))
                .toModelStringAsResult()
                .computeJSON();
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        return Model.of(kripke)
                .toKripkeStructure()
                .toFormulaStringAsResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripke2compactFormula/:kripke/:steps")
    public String kripke2CompactFormula(String kripke, String steps) {
        return Model.of(kripke)
                .toKripkeStructure()
                .toKripkeTruthTable()
                .toFormulaStringWithEncodingStartAndEndAsResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripke2compactQBFFormula/:kripke/:steps")
    public String kripke2QBFFormula(String kripke, String steps) {
        return Model.of(kripke)
                .toKripkeStructure()
                .toKripkeTruthTable()
                .toQBFStringAsResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/simplify/:formula")
    public String simplify(String formula) {
        return Simplification.ofAsResult(formula)
                .computeJSON();
    }
}

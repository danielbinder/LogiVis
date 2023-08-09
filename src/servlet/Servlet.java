package servlet;

import model.Model;
import model.kripke.KripkeGenerator;
import bool.interpreter.BruteForceSolver;
import bool.interpreter.Simplification;
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
        return BruteForceSolver.solveWithResult(formula)
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
        return BruteForceSolver.solveAllWithResult(formula)
                .computeJSON();
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return Model.of(KripkeGenerator.generate(params, 10))
                .toModelStringWithResult()
                .computeJSON();
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        return Model.of(kripke)
                .toKripkeStructure()
                .toFormulaStringWithResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripke2compactFormula/:kripke/:steps")
    public String kripke2CompactFormula(String kripke, String steps) {
        return Model.of(kripke)
                .toKripkeStructure()
                .toKripkeTruthTable()
                .toFormulaStringWithResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripke2compactQBFFormula/:kripke/:steps")
    public String kripke2QBFFormula(String kripke, String steps) {
        return Model.of(kripke)
                .toKripkeStructure()
                .toKripkeTruthTable()
                .toQBFStringWithResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/simplify/:formula")
    public String simplify(String formula) {
        return Simplification.ofWithResult(formula)
                .computeJSON();
    }
}

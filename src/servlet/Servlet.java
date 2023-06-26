package servlet;

import generator.Generator;
import model.kripke.KripkeStructure;
import model.kripke.KripkeTruthTable;
import interpreter.BruteForceSolver;
import interpreter.Simplification;
import rest.GET;
import rest.REST;
import temporal.solver.CTLSolver;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
        temporal.model.KripkeStructure kripkeStructure = new temporal.model.KripkeStructure(formattedModel);
        CTLSolver solver = new CTLSolver(kripkeStructure);

        TreeMap<String, String> results = solver.getSatisfyingStates(formula);
        results.put("steps", solver.getSolverSteps().replaceAll(System.lineSeparator(), "_"));

        return resultToJSON(results);
    }

    @GET("/solveAll/:formula")
    public String solveAll(String formula) {
        return BruteForceSolver.solveAllWithResult(formula)
                .computeJSON();
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return Generator.generateKripkeStructureWithResult(params, 10)
                .computeJSON();
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        return KripkeStructure.fromString(kripke.replace(",", ";"))
                .toFormulaStringWithResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripke2compactFormula/:kripke/:steps")
    public String kripke2CompactFormula(String kripke, String steps) {
        return new KripkeTruthTable(kripke.replace(",", ";"))
                .toFormulaStringWithResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripke2compactQBFFormula/:kripke/:steps")
    public String kripke2QBFFormula(String kripke, String steps) {
        return new KripkeTruthTable(kripke.replace(",", ";"))
                .toQBFStringWithResult(Integer.parseInt(steps))
                .computeJSON();
    }

    @GET("/kripkeString2ModelString/:kripkeString")
    public String kripkeString2ModelString(String kripkeString) {
        return resultToJSON(Map.of("result", KripkeStructure
                                .fromString(kripkeString.replace(",", ";"))
                                .toOtherKripke()
                                .toModelString()
                                .replaceAll(";", "_")
                                .replaceAll("\n", "+")));
    }

    @GET("/simplify/:formula")
    public String simplify(String formula) {
        return Simplification.ofWithResult(formula)
                .computeJSON();
    }

    private static String resultToJSON(Map<String, String> map) {
        if(map == null) return "{\"result\":\"unsatisfiable\"}";
        return map.entrySet().stream()
                        .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
                        .collect(Collectors.joining(",", "{", "}"));
    }
}

package servlet;

import generator.Generator;
import kripke.KripkeStructure;
import kripke.KripkeTruthTable;
import interpreter.BruteForceSolver;
import interpreter.Simplification;
import lexer.Lexer;
import parser.Parser;
import rest.GET;
import rest.REST;
import temporal.solver.CTLSolver;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Servlet {
    private static final int SERVLET_PORT = 4000;
    private static final int APP_PORT = 3000;
    private static final Parser PARSER = new Parser();

    public static void main(String[] args) {
        REST.start(SERVLET_PORT);
        System.out.println("Server started; go to: http://localhost:" + APP_PORT);
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return resultToJSON(BruteForceSolver.solve(PARSER.parse(Lexer.tokenize(formula))));
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
        return resultToJSON(BruteForceSolver.solveAll(PARSER.parse(Lexer.tokenize(formula))));
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return resultToJSON(Map.of("result", Generator.generateKripkeStructure(params, 10).toString()));
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        String rawKripke = kripke.replace(",", ";");
        return resultToJSON(Map.of("result", KripkeStructure.fromString(rawKripke)
                                                            .toFormulaString(Integer.parseInt(steps))
                                                            .replaceAll("\n", " ")));
    }

    @GET("/kripke2CompactFormula/:kripke/:steps")
    public String kripke2CompactFormula(String kripke, String steps) {
        String rawKripke = kripke.replace(",", ";");
        return resultToJSON(Map.of("result", new KripkeTruthTable(KripkeStructure.fromString(rawKripke))
                                                            .toFormulaString(Integer.parseInt(steps))
                                                            .replaceAll("\n", " ")));
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
        return resultToJSON(Map.of("result", Simplification.of(PARSER.parse(Lexer.tokenize(formula))).toString()));
    }

    private static String resultToJSON(List<Map<String, String>> assignments) {
        if(assignments == null) return "{\"result\":\"unsatisfiable\"}";
        if(assignments.stream().anyMatch(m -> "valid".equals(m.get("result")))) return "{\"result\":\"valid\"}";
        return "{" + assignments.stream()
                        .map(a -> "\"assignment_" + assignments.indexOf(a) + "\":" + resultToJSON(a))
                        .collect(Collectors.joining(",")) + "}";
    }


    private static String resultToJSON(Map<String, String> map) {
        if(map == null) return "{\"result\":\"unsatisfiable\"}";
        return map.entrySet().stream()
                        .map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\"")
                        .collect(Collectors.joining(",", "{", "}"));
    }
}

package servlet;

import generator.Generator;
import generator.kripke.KripkeStructure;
import interpreter.BruteForceSolver;
import interpreter.Simplification;
import lexer.Lexer;
import parser.Parser;
import rest.GET;
import rest.REST;
import temporal.solver.CTLSolver;

import java.util.Map;
import java.util.TreeMap;

public class Servlet {
    private static final int SERVLET_PORT = 4000;
    private static final int APP_PORT = 3000;
    private static final Parser PARSER = new Parser();

    public static void main(String[] args) {
        REST.start(SERVLET_PORT);
        System.out.println("Click: http://localhost:" + APP_PORT);
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return BruteForceSolver.resultToJSON(BruteForceSolver.solve(PARSER.parse(Lexer.tokenize(formula))));
    }

    @GET("/solveCTL/:formula/:model")
    public String solveCTL(String formula, String model) {
        String formattedModel = model.replace("_", ";");
        temporal.model.KripkeStructure kripkeStructure = new temporal.model.KripkeStructure(formattedModel);
        CTLSolver solver = new CTLSolver(kripkeStructure);

        TreeMap<String, String> results = solver.getSatisfyingStates(formula);
        results.put("steps", solver.getSolverSteps().replaceAll(System.lineSeparator(), "_"));

        return BruteForceSolver.resultToJSON(results);
    }

    @GET("/solveAll/:formula")
    public String solveAll(String formula) {
        return BruteForceSolver.resultToJSON(BruteForceSolver.solveAll(PARSER.parse(Lexer.tokenize(formula))));
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return BruteForceSolver.resultToJSON(Map.of("result", Generator.generateKripkeStructure(params, 10).toString()));
    }

    @GET("/kripke2formula/:kripke/:steps")
    public String kripke2formula(String kripke, String steps)   {
        String rawKripke = kripke.replace(",", ";");
        return BruteForceSolver.resultToJSON(Map.of("result",
                                                    KripkeStructure.fromString(rawKripke)
                                                            .toFormulaString(Integer.parseInt(steps))
                                                            .replaceAll("\n", " ")));
    }

    @GET("/simplify/:formula")
    public String simplify(String formula) {
        return BruteForceSolver.resultToJSON(Map.of("result",
                                                    Simplification.of(PARSER.parse(Lexer.tokenize(formula))).toString()));
    }
}

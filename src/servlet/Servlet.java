package servlet;

import generator.Generator;
import interpreter.BruteForceSolver;
import lexer.Lexer;
import parser.Parser;
import rest.GET;
import rest.REST;

import java.util.Map;

public class Servlet {
    private static final int PORT = 4000;
    private static final Parser PARSER = new Parser();

    public static void main(String[] args) {
        REST.start(PORT);
        System.out.println("Click: http://localhost:" + PORT);
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return BruteForceSolver.resultToJSON(BruteForceSolver.solve(PARSER.parse(Lexer.tokenize(formula))));
    }

    @GET("/solveAll/:formula")
    public String solveAll(String formula) {
        return BruteForceSolver.resultToJSON(BruteForceSolver.solveAll(PARSER.parse(Lexer.tokenize(formula))));
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return BruteForceSolver.resultToJSON(Map.of("result", Generator.generateKripkeStructure(params).toString()));
    }

}

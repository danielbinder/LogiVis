package servlet;

import generator.Generator;
import interpreter.Interpreter;
import lexer.Lexer;
import parser.Parser;
import rest.GET;
import rest.REST;

public class Servlet {
    private static final int PORT = 4000;
    private static final Parser PARSER = new Parser();

    public static void main(String[] args) {
        REST.start(PORT);
        System.out.println("Click: http://localhost:" + PORT + "/solve/var1");
    }

    @GET("/solve/:formula")
    public String solve(String formula) {
        return REST.toJSON(Interpreter.solve(PARSER.parse(Lexer.tokenize(formula))));
    }

    @GET("/generate/:params")
    public String generate(String params) {
        return Generator.generateFormula(params).toString();
    }
}

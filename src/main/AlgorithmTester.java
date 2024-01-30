package main;

import marker.RestEndpoint;
import model.parser.Model;
import model.variant.finite.interpreter.ImplementationValidator;
import util.Logger;
import util.Result;
import util.rest.GET;
import util.rest.REST;

import java.io.File;

import static marker.AlgorithmImplementation.USER;
import static util.rest.REST.preprocess;

public class AlgorithmTester implements RestEndpoint {
    ImplementationValidator validator = new ImplementationValidator();

    public static void run() {
        REST.start();
        Logger.info("Started AlgorithmTester");
    }

    @GET("/validate/:methodName/:name/:compact")
    public String validate(String compact, String methodName, String name) {
        return new Result(() -> validator.validate(methodName, name, Boolean.parseBoolean(compact)))
                .computeJSON();
    }

    @GET("/validateAll/:name/:compact")
    public String validateAll(String compact, String name) {
        return new Result(() -> validator.validateAll(name, Boolean.parseBoolean(compact)),
                () -> String.format("Created file '%s.report' in directory '%sresources'",
                        name, System.getProperty("user.dir") + File.separator))
                .computeJSON();
    }

    @GET("/isDeterministic/:automaton")
    public String isDeterministic(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().isDeterministic() ? "true" : "false")
                .computeJSON();
    }

    @GET("/isComplete/:automaton")
    public String isComplete(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().isComplete() ? "true" : "false")
                .computeJSON();
    }

    @GET("/isEquivalent/:automaton1/:automaton2")
    public String isEquivalent(String automaton1, String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1))
                .toFiniteAutomaton()
                .isEquivalent(Model.of(preprocess(automaton2)).toFiniteAutomaton()) ? "true" : "false")
                .computeJSON();
    }

    @GET("areReachable/:automaton")
    public String areReachable(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .areReachable() ? "true" : "false")
                .computeJSON();
    }

    @GET("/toProductAutomaton/:automaton1/:automaton2")
    public String toProductAutomaton(String automaton1, String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1)).toFiniteAutomaton()
                .toProductAutomaton(Model.of(preprocess(automaton2)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/isSimulatedBy/:automaton1/:automaton2")
    public String isSimulatedBy(String automaton1, String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1))
                .toFiniteAutomaton()
                .isSimulatedBy(Model.of(preprocess(automaton2)).toFiniteAutomaton()) ? "true" : "false")
                .computeJSON();
    }

    @GET("/toPowerAutomaton/:automaton")
    public String toPowerAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toPowerAutomaton()
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/toComplementAutomaton/:automaton")
    public String toComplementAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toComplementAutomaton()
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/toSinkAutomaton/:automaton")
    public String toSinkAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toSinkAutomaton()
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/toOracleAutomaton/:automaton")
    public String toOracleAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toOracleAutomaton()
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/toOptimisedOracleAutomaton/:automaton")
    public String toOptimisedOracleAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toOptimisedOracleAutomaton()
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/testIsDeterministic/:automaton")
    public String testIsDeterministic(String automaton) {
        return new Result(() -> USER.isDeterministic(Model.of(preprocess(automaton)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GET("/testIsComplete/:automaton")
    public String testIsComplete(String automaton) {
        return new Result(() -> USER.isComplete(Model.of(preprocess(automaton)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GET("/testIsEquivalent/:automaton1/:automaton2")
    public String testIsEquivalent(String automaton1, String automaton2) {
        return new Result(() -> USER.isEquivalent(Model.of(preprocess(automaton1)).toFiniteAutomaton(),
                                                  Model.of(preprocess(automaton2)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GET("/testToProductAutomaton/:automaton1/:automaton2")
    public String testToProductAutomaton(String automaton1, String automaton2) {
        return new Result(() -> USER.toProductAutomaton(Model.of(preprocess(automaton1)).toFiniteAutomaton(),
                                                        Model.of(preprocess(automaton2)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/testToPowerAutomaton/:automaton")
    public String testToPowerAutomaton(String automaton) {
        return new Result(() -> USER.toPowerAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/testToComplementAutomaton/:automaton")
    public String testToComplementAutomaton(String automaton) {
        return new Result(() -> USER.toComplementAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/testToSinkAutomaton/:automaton")
    public String testToSinkAutomaton(String automaton) {
        return new Result(() -> USER.toSinkAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/testToOracleAutomaton/:automaton")
    public String testToOracleAutomaton(String automaton) {
        return new Result(() -> USER.toOracleAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GET("/testToOptimisedOracleAutomaton/:automaton")
    public String testToOptimisedOracleAutomaton(String automaton) {
        return new Result(() -> USER.toOptimisedOracleAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }
}

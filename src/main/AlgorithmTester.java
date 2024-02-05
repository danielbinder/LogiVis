package main;

import marker.RestEndpoint;
import model.parser.Model;
import model.variant.finite.interpreter.ImplementationValidator;
import model.variant.finite.interpreter.SampleImplementation;
import util.Logger;
import util.Result;
import util.rest.GET;
import util.rest.REST;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

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
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().isDeterministic() ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/isComplete/:automaton")
    public String isComplete(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().isComplete() ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/isEquivalent/:automaton1/:automaton2")
    public String isEquivalent(String automaton1, String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1))
                .toFiniteAutomaton()
                .isEquivalent(Model.of(preprocess(automaton2)).toFiniteAutomaton()) ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("areReachable/:automaton")
    public String areReachable(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .areReachable() ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/toProductAutomaton/:automaton1/:automaton2")
    public String toProductAutomaton(String automaton1, String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1)).toFiniteAutomaton()
                .toProductAutomaton(Model.of(preprocess(automaton2)).toFiniteAutomaton())
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
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
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/toComplementAutomaton/:automaton")
    public String toComplementAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toComplementAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/toSinkAutomaton/:automaton")
    public String toSinkAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toSinkAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/toOracleAutomaton/:automaton")
    public String toOracleAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toOracleAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("/toOptimisedOracleAutomaton/:automaton")
    public String toOptimisedOracleAutomaton(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toOptimisedOracleAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GET("getStronglyConnectedComponents/:automaton")
    public String getStronglyConnectedComponents(String automaton) {
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().getStronglyConnectedComponents()
                    .stream()
                    .map(set -> set.stream()
                            .map(state -> state.name)
                            .collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("\n")),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()),
                          Map.of(String::isEmpty, "No strongly connected components!"))
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

    @GET("/testIsSimulatedBy/:automaton1/:automaton2")
    public String testIsSimulatedBy(String automaton1, String automaton2) {
        return new Result(() -> USER.isSimulatedBy(Model.of(preprocess(automaton1)).toFiniteAutomaton(),
                        Model.of(preprocess(automaton2)).toFiniteAutomaton())
                ? "true"
                : "false")
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

    @GET("testGetStronglyConnectedComponents/:automaton")
    public String testGetStronglyConnectedComponents(String automaton) {
        return new Result(() -> USER.getStronglyConnectedComponents(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .stream()
                .map(set -> set.stream()
                        .map(state -> state.name)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n")))
                .computeJSON();
    }
}

package algorithmTester;

import marker.RestEndpoint;
import model.parser.Model;
import servlet.Result;
import servlet.rest.GET;
import servlet.rest.REST;

import static marker.AlgorithmImplementation.USER;
import static servlet.rest.REST.preprocess;

public class AlgorithmTester implements RestEndpoint {
    public static void run() {
        REST.start();
        System.out.println("[INFO] Started AlgorithmTester");
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

    @GET("/toProductAutomaton/:automaton1/:automaton2")
    public String toProductAutomaton(String automaton1, String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1)).toFiniteAutomaton()
                .toProductAutomaton(Model.of(preprocess(automaton2)).toFiniteAutomaton())
                .toModel()
                .toModelString())
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

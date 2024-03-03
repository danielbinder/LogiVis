package main;

import model.parser.Model;
import model.variant.finite.interpreter.ImplementationValidator;
import model.variant.finite.interpreter.SampleImplementation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import util.Result;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import static marker.AlgorithmImplementation.USER;

@RestController
public class AlgorithmTester implements RestEndpoint {
    private static final ImplementationValidator validator = new ImplementationValidator();

    @GetMapping("/validate/{methodName}/{name}/{compact}")
    public String validate(@PathVariable String methodName, @PathVariable String name, @PathVariable String compact) {
        return new Result(() -> validator.validate(methodName, name, Boolean.parseBoolean(compact)))
                .computeJSON();
    }

    @GetMapping("/validateAll/{name}/{compact}")
    public String validateAll(@PathVariable String name, @PathVariable String compact) {
        return new Result(() -> validator.validateAll(name, Boolean.parseBoolean(compact)),
                () -> String.format("Created file '%s.report' in directory '%sresources'",
                        name, System.getProperty("user.dir") + File.separator))
                .computeJSON();
    }

    @GetMapping("/isDeterministic/{automaton}")
    public String isDeterministic(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().isDeterministic() ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/isComplete/{automaton}")
    public String isComplete(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton)).toFiniteAutomaton().isComplete() ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/isEquivalent/{automaton1}/{automaton2}")
    public String isEquivalent(@PathVariable String automaton1, @PathVariable String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1))
                .toFiniteAutomaton()
                .isEquivalent(Model.of(preprocess(automaton2)).toFiniteAutomaton()) ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/areReachable/{automaton}")
    public String areReachable(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .areReachable() ? "true" : "false",
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/toProductAutomaton/{automaton1}/{automaton2}")
    public String toProductAutomaton(@PathVariable String automaton1, @PathVariable String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1)).toFiniteAutomaton()
                .toProductAutomaton(Model.of(preprocess(automaton2)).toFiniteAutomaton())
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/isSimulatedBy/{automaton1}/{automaton2}")
    public String isSimulatedBy(@PathVariable String automaton1, @PathVariable String automaton2) {
        return new Result(() -> Model.of(preprocess(automaton1))
                .toFiniteAutomaton()
                .isSimulatedBy(Model.of(preprocess(automaton2)).toFiniteAutomaton()) ? "true" : "false")
                .computeJSON();
    }

    @GetMapping("/toPowerAutomaton/{automaton}")
    public String toPowerAutomaton(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toPowerAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/toComplementAutomaton/{automaton}")
    public String toComplementAutomaton(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toComplementAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/toSinkAutomaton/{automaton}")
    public String toSinkAutomaton(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toSinkAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/toOracleAutomaton/{automaton}")
    public String toOracleAutomaton(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toOracleAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("/toOptimisedOracleAutomaton/{automaton}")
    public String toOptimisedOracleAutomaton(@PathVariable String automaton) {
        return new Result(() -> Model.of(preprocess(automaton))
                .toFiniteAutomaton()
                .toOptimisedOracleAutomaton()
                .toModel()
                .toModelString(),
                          () -> String.join("\n", SampleImplementation.getSolutionInformation()))
                .computeJSON();
    }

    @GetMapping("getStronglyConnectedComponents/{automaton}")
    public String getStronglyConnectedComponents(@PathVariable String automaton) {
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

    @GetMapping("/testIsDeterministic/{automaton}")
    public String testIsDeterministic(@PathVariable String automaton) {
        return new Result(() -> USER.isDeterministic(Model.of(preprocess(automaton)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GetMapping("/testIsComplete/{automaton}")
    public String testIsComplete(@PathVariable String automaton) {
        return new Result(() -> USER.isComplete(Model.of(preprocess(automaton)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GetMapping("/testIsEquivalent/{automaton1}/{automaton2}")
    public String testIsEquivalent(@PathVariable String automaton1, @PathVariable String automaton2) {
        return new Result(() -> USER.isEquivalent(Model.of(preprocess(automaton1)).toFiniteAutomaton(),
                                                  Model.of(preprocess(automaton2)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GetMapping("/testToProductAutomaton/{automaton1}/{automaton2}")
    public String testToProductAutomaton(@PathVariable String automaton1, @PathVariable String automaton2) {
        return new Result(() -> USER.toProductAutomaton(Model.of(preprocess(automaton1)).toFiniteAutomaton(),
                                                        Model.of(preprocess(automaton2)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/testIsSimulatedBy/{automaton1}/{automaton2}")
    public String testIsSimulatedBy(@PathVariable String automaton1, @PathVariable String automaton2) {
        return new Result(() -> USER.isSimulatedBy(Model.of(preprocess(automaton1)).toFiniteAutomaton(),
                        Model.of(preprocess(automaton2)).toFiniteAutomaton())
                ? "true"
                : "false")
                .computeJSON();
    }

    @GetMapping("/testToPowerAutomaton/{automaton}")
    public String testToPowerAutomaton(@PathVariable String automaton) {
        return new Result(() -> USER.toPowerAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/testToComplementAutomaton/{automaton}")
    public String testToComplementAutomaton(@PathVariable String automaton) {
        return new Result(() -> USER.toComplementAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/testToSinkAutomaton/{automaton}")
    public String testToSinkAutomaton(@PathVariable String automaton) {
        return new Result(() -> USER.toSinkAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/testToOracleAutomaton/{automaton}")
    public String testToOracleAutomaton(@PathVariable String automaton) {
        return new Result(() -> USER.toOracleAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/testToOptimisedOracleAutomaton/{automaton}")
    public String testToOptimisedOracleAutomaton(@PathVariable String automaton) {
        return new Result(() -> USER.toOptimisedOracleAutomaton(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("testGetStronglyConnectedComponents/{automaton}")
    public String testGetStronglyConnectedComponents(@PathVariable String automaton) {
        return new Result(() -> USER.getStronglyConnectedComponents(Model.of(preprocess(automaton)).toFiniteAutomaton())
                .stream()
                .map(set -> set.stream()
                        .map(state -> state.name)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n")))
                .computeJSON();
    }
}

import bool.parser.logicnode.LogicNode;
import bool.variant.cnf.interpreter.AssignmentTester;
import bool.variant.cnf.interpreter.CDCLSolver;
import bool.variant.cnf.interpreter.CNFSolver;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.FileHelper;
import util.Logger;
import util.Timeout;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Disabled
public class CNFSolverTest {
    private static final String PATH = "src/test/resources/cnf";
    private static final Map<String, CNFSolver> SOLVERS = Map.of(
//            "RecursiveDPLLSolver", new RecursiveDPLLSolver(),
//            "NonRecursiveLiteralWatchingDPLLSolver", new NonRecursiveLiteralWatchingDPLLSolver(),
            "CDCLSolver", new CDCLSolver()
    );

    @Test
    public void test() {
        Logger.SAVE_LOG = true;
        for(var entry : SOLVERS.entrySet()) {
            String solverName = entry.getKey();
            CNFSolver solver = entry.getValue();
            Logger.info("Testing " + solverName);
            int sat = 0;
            int unsat = 0;
            int error = 0;
            int timedOut = 0;

            try {
                for(Path path : FileHelper.readAll(PATH + "/in")) {
                    if(!path.toString().contains("prime961")) continue;
                    Logger.info("Reading " + path);
                    Conjunction conjunction = Conjunction.of(FileHelper.read(path.toString()));
                    Logger.info("Conjunction: " + conjunction);

                    var result = Timeout.of(() -> solver.solve(conjunction.clone()), 20, TimeUnit.SECONDS);

                    String solution;
                    if(result == null) {
                        solution = "timed out";
                        timedOut++;
                    } else if(result.isEmpty()) {
                        unsat++;
                        solution = "unsatisfiable";
                        FileHelper.write(
                                PATH + "/out/" + path.getFileName().toString()
                                        .replace(".in", "_" + solverName + ".out"),
                                "unsatisfiable");
                    } else {
                        solution = result.entrySet().stream()
                                .sorted(Comparator.comparing(e -> e.getKey().name()))
                                .map(e -> (e.getValue() ? "" : "!") + e.getKey().name())
                                .collect(Collectors.joining(", "));
                        if(AssignmentTester.isValidAssignment(conjunction, result)) {
                            sat++;
                            Logger.info("Verified correctness of assignment!");
                            FileHelper.write(
                                    PATH + "/out/" + path.getFileName().toString()
                                            .replace(".in", "_" + solverName + ".out"),
                                    result.entrySet().stream()
                                            .map(e -> (e.getValue() ? "" : "-") + (conjunction.variables.indexOf(e.getKey()) + 1))
                                            .collect(Collectors.joining(" ")));
                        } else {
                            error++;
                            Logger.error("Assignment is wrong!");
                        }
                    }

                    Logger.info("Result: " + solution);
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            Logger.info(solverName + ": " + sat + " sat, " + unsat + " unsat, " + timedOut + " timedOut, " + error + " error");
            Logger.info(solverName + ": " + (sat + unsat) + " solved out of " + (sat + unsat + timedOut + error) + " total");
            FileHelper.write(PATH + "/log/" + solverName + "_" + LocalDateTime.now().toString()
                                     .replace(":", "_")
                                     .replaceAll("\\.\\d+", "") + ".log",
                             Logger.consumeSavedLog());
        }

        Logger.SAVE_LOG = false;
    }

    @Test
    void testProblematicFormulas() {
        List<String> problematicFormulas = List.of("!(!a | b) -> (c & (c))");


        for(var entry : SOLVERS.entrySet()) {
            String solverName = entry.getKey();
            CNFSolver solver = entry.getValue();
            Logger.info("Testing " + solverName);

            for(String formula : problematicFormulas) {
                Conjunction conjunction = LogicNode.of(formula).toCNF();
                solver.solve(conjunction);
            }
        }
    }
}

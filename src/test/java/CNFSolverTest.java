import bool.parser.logicnode.LogicNode;
import bool.variant.cnf.interpreter.AssignmentTester;
import bool.variant.cnf.interpreter.CNFSolver;
import bool.variant.cnf.interpreter.NonRecursiveLiteralWatchingDPLLSolver;
import bool.variant.cnf.interpreter.RecursiveDPLLSolver;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Disabled
public class CNFSolverTest {
    private static final String PATH = "src/test/resources/cnf";
    private static final Map<String, CNFSolver> SOLVERS = Map.of(
            "RecursiveDPLLSolver", new RecursiveDPLLSolver(),
            "NonRecursiveLiteralWatchingDPLLSolver", new NonRecursiveLiteralWatchingDPLLSolver()
    );

    @Test
    public void test() {
        Logger.SAVE_LOG = true;
        for(var entry : SOLVERS.entrySet()) {
            String solverName = entry.getKey();
            CNFSolver solver = entry.getValue();
            Logger.info("Testing " + solverName);
            AtomicInteger solved = new AtomicInteger();
            AtomicInteger timedOut = new AtomicInteger();

            try {
                for(Path path : FileHelper.readAll(PATH + "/in")) {
//                    if(!path.toString().contains("full2no10")) continue;
                    Logger.info("Reading " + path);
                    Conjunction conjunction = Conjunction.of(FileHelper.read(path.toString()));
                    Logger.info("Conjunction: " + conjunction);

                    var result = Timeout.of(() -> solver.solve(conjunction.clone()), 5, TimeUnit.SECONDS);

                    String fileContent;
                    if(result == null) {
                        fileContent = "timed out";
                        timedOut.getAndIncrement();
                    } else if(result.isEmpty()) {
                        fileContent = "unsatisfiable";
                        solved.getAndIncrement();
                    } else {
                        fileContent = result.entrySet().stream()
                                .sorted(Comparator.comparing(e -> e.getKey().name()))
                                .map(e -> (e.getValue() ? "" : "!") + e.getKey().name())
                                .collect(Collectors.joining(", "));
                        if(AssignmentTester.isValidAssignment(conjunction, result)) {
                            solved.getAndIncrement();
                            Logger.info("Verified correctness of assignment!");
                        } else Logger.error("Assignment is wrong!");
                    }

                    Logger.info("Result: " + fileContent);
                    FileHelper.write(
                            PATH + "/out/" + path.getFileName().toString()
                                    .replace(".in", "_" + solverName + ".out"),
                            fileContent);
                    }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            Logger.info(solverName + ": " + solved.get() + " solved, " + timedOut.get() + " timed out");
            FileHelper.write(PATH + "/log/" + solverName + "_" + LocalDateTime.now().toString()
                                     .replace(":", "_")
                                     .replaceAll("\\.\\d+", "") + ".log",
                             Logger.consumeSavedLog());
        }

        Logger.SAVE_LOG = false;
    }

    @Test
    void testProblematicFormulas() {
        // TODO: non rec DPLL does not seem to work with this:
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

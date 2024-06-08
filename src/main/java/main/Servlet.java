package main;

import bool.generator.BooleanGenerator;
import bool.interpreter.BruteForceSolver;
import bool.interpreter.Parenthesiser;
import bool.interpreter.Simplification;
import bool.parser.logicnode.LogicNode;
import bool.variant.cnf.interpreter.CDCLSolver;
import bool.variant.cnf.interpreter.IncrementalCNFSolver;
import bool.variant.cnf.interpreter.NonRecursiveLiteralWatchingDPLLSolver;
import bool.variant.cnf.interpreter.RecursiveDPLLSolver;
import bool.variant.cnf.parser.cnfnode.Conjunction;
import ctl.generator.CTLGenerator;
import ctl.interpreter.CTLSolver;
import ctl.parser.ctlnode.CTLNode;
import model.interpreter.ModelTracer;
import model.parser.Model;
import model.variant.finite.FiniteAutomatonGenerator;
import model.variant.finite.interpreter.ImplementationValidator;
import model.variant.kripke.KripkeGenerator;
import model.variant.kripke.KripkeTruthTable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import util.Result;

import java.util.List;
import java.util.Map;

@RestController
public class Servlet implements RestEndpoint {
    private static final ImplementationValidator validator = new ImplementationValidator();

    @GetMapping("/solve/brute/{formula}")
    public String solveBruteForce(@PathVariable String formula) {
        return new Result(() -> new BruteForceSolver(preprocess(formula)),
                          solver -> List.of(solver.solve()),
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/solve/DPLLrec/{formula}")
    public String solveDPLLRec(@PathVariable String formula) {
        Conjunction conjunction = LogicNode.of(preprocess(formula)).toCNF();
        return new Result(RecursiveDPLLSolver::new,
                          solver -> List.of(solver.solveAndTransform(LogicNode.of(preprocess(formula)).toCNF())),
                          solver -> "Equisatisfiable CNF:\n" + conjunction,
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/solve/DPLLnonrec/{formula}")
    public String solveDPLLNonRec(@PathVariable String formula) {
        Conjunction conjunction = LogicNode.of(preprocess(formula)).toCNF();
        return new Result(NonRecursiveLiteralWatchingDPLLSolver::new,
                          solver -> List.of(solver.solveAndTransform(LogicNode.of(preprocess(formula)).toCNF())),
                          solver -> "Equisatisfiable CNF:\n" + conjunction,
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/solve/CDCL/{formula}")
    public String solveCDCL(@PathVariable String formula) {
        Conjunction conjunction = LogicNode.of(preprocess(formula)).toCNF();
        return new Result(CDCLSolver::new,
                          solver -> List.of(solver.solveAndTransform(LogicNode.of(preprocess(formula)).toCNF())),
                          solver -> "Equisatisfiable CNF:\n" + conjunction,
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/solveAll/brute/{formula}")
    public String solveAllBruteForce(@PathVariable String formula) {
        return new Result(() -> new BruteForceSolver(preprocess(formula)),
                          BruteForceSolver::solveAll,
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable",
                                 solver -> solver.valid, "valid"))
                .computeJSON();
    }

    @GetMapping("/solveAll/DPLLrec/{formula}")
    public String solveAllDPLLRec(@PathVariable String formula) {
        Conjunction conjunction = LogicNode.of(preprocess(formula)).toCNF();
        return new Result(() -> new IncrementalCNFSolver(new RecursiveDPLLSolver()),
                          solver -> solver.solveAllAndTransform(LogicNode.of(preprocess(formula)).toCNF()),
                          solver -> "Equisatisfiable CNF:\n" + conjunction,
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/solveAll/DPLLnonrec/{formula}")
    public String solveAllDPLLNonRec(@PathVariable String formula) {
        Conjunction conjunction = LogicNode.of(preprocess(formula)).toCNF();
        return new Result(() -> new IncrementalCNFSolver(new NonRecursiveLiteralWatchingDPLLSolver()),
                          solver -> solver.solveAllAndTransform(LogicNode.of(preprocess(formula)).toCNF()),
                          solver -> "Equisatisfiable CNF:\n" + conjunction,
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/solveAll/CDCL/{formula}")
    public String solveAllCDCL(@PathVariable String formula) {
        Conjunction conjunction = LogicNode.of(preprocess(formula)).toCNF();
        return new Result(() -> new IncrementalCNFSolver(new CDCLSolver()),
                          solver -> solver.solveAllAndTransform(LogicNode.of(preprocess(formula)).toCNF()),
                          solver -> "Equisatisfiable CNF:\n" + conjunction,
                          Map.of(solver -> solver.unsatisfiable, "unsatisfiable"))
                .computeJSON();
    }

    @GetMapping("/parenthesise/{formula}")
    public String parenthesise(@PathVariable String formula) {
        return new Result(() -> Parenthesiser.addNecessaryParenthesis(LogicNode.of(preprocess(formula))))
                .computeJSON();
    }

    @GetMapping("/parenthesiseAll/{formula}")
    public String parenthesiseAll(@PathVariable String formula) {
        return new Result(() -> LogicNode.of(preprocess(formula)).toString())
                .computeJSON();
    }

    @GetMapping("/trace/{kripke}")
    public String trace(@PathVariable String kripke) {
        return new Result(() -> Model.of(preprocess(kripke)).toModelTracer(),
                          ModelTracer::trace,
                          tracer -> String.join("\n", tracer.solutionInfo))
                .computeJSON();
    }

    @GetMapping("/shortestTrace/{kripke}")
    public String shortestTrace(@PathVariable String kripke) {
        return new Result(() -> Model.of(preprocess(kripke)).toModelTracer(),
                          ModelTracer::shortestTrace,
                          tracer -> String.join("\n", tracer.solutionInfo))
                .computeJSON();
    }

    @GetMapping("/solveCTL/{formula}/{model}")
    public String solveCTL(@PathVariable String formula, @PathVariable String model) {
        return new Result(() -> new CTLSolver(Model.of(preprocess(model)).toKripkeStructure()),
                          solver -> List.of(solver.solve(CTLNode.of(preprocess(formula)))),
                          solver -> String.join("\n", solver.solutionInfo),
                          Map.of())
                .computeJSON();
    }

    @GetMapping("/generateKripke/{nodes}/{initialNodes}/{variables}/{minSuccessors}/{maxSuccessors}/{allReachable}")
    public String generateKripke(@PathVariable String nodes, @PathVariable String initialNodes,
                                 @PathVariable String variables, @PathVariable String minSuccessors,
                                 @PathVariable String maxSuccessors, @PathVariable String allReachable) {
        return new Result(() -> Model.of(KripkeGenerator.generate(Integer.parseInt(nodes),
                                                                  Integer.parseInt(initialNodes),
                                                                  Integer.parseInt(variables),
                                                                  Integer.parseInt(minSuccessors),
                                                                  Integer.parseInt(maxSuccessors),
                                                                  Boolean.parseBoolean(allReachable)))
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/generateFiniteAutomaton/{nodes}/{initialNodes}/{finalNodes}/{alphabetSize}/{minSuccessors}/{maxSuccessors}/{allReachable}")
    public String generateFiniteAutomaton(@PathVariable String nodes, @PathVariable String initialNodes,
                                          @PathVariable String finalNodes, @PathVariable String alphabetSize,
                                          @PathVariable String minSuccessors, @PathVariable String maxSuccessors,
                                          @PathVariable String allReachable) {
        return new Result(() -> FiniteAutomatonGenerator.generate(Integer.parseInt(nodes),
                                                                           Integer.parseInt(initialNodes),
                                                                           Integer.parseInt(finalNodes),
                                                                           Integer.parseInt(alphabetSize),
                                                                           Integer.parseInt(minSuccessors),
                                                                           Integer.parseInt(maxSuccessors),
                                                                           Boolean.parseBoolean(allReachable))
                .toModel()
                .toModelString())
                .computeJSON();
    }

    @GetMapping("/generateBooleanFormula/{variables}/{operators}")
    public String generateFormula(@PathVariable String variables, @PathVariable String operators) {
        return new Result(() -> BooleanGenerator.generate(variables, operators))
                .computeJSON();
    }

    @GetMapping("/generateCTLFormula/{variables}/{operators}")
    public String generateCTLFormula(@PathVariable String variables, @PathVariable String operators) {
        return new Result(() -> CTLGenerator.generate(variables, operators))
                .computeJSON();
    }

    @GetMapping("/kripke2formula/{kripke}/{upUntil}/{steps}")
    public String kripke2formula(@PathVariable String kripke, @PathVariable String upUntil, @PathVariable String steps)   {
        return new Result(() -> Model.of(preprocess(kripke))
                .toKripkeStructure()
                .toFormulaString(Boolean.parseBoolean(upUntil), Integer.parseInt(steps)))
                .computeJSON();
    }

    @GetMapping("/kripke2compactFormula/{kripke}/{upUntil}/{steps}")
    public String kripke2CompactFormula(@PathVariable String kripke, @PathVariable String upUntil, @PathVariable String steps) {
        return new Result(() -> Model.of(preprocess(kripke))
                                 .toKripkeStructure()
                                 .toKripkeTruthTable(),
                         ktt -> ktt.toFormulaStringWithEncodingStartAndEnd(Boolean.parseBoolean(upUntil), Integer.parseInt(steps)),
                         KripkeTruthTable::toString)
                .computeJSON();
    }

    @GetMapping("/kripke2compactQBFFormula/{kripke}/{upUntil}/{steps}")
    public String kripke2QBFFormula(@PathVariable String kripke, @PathVariable String upUntil, @PathVariable String steps) {
        return new Result(() -> Model.of(preprocess(kripke))
                                 .toKripkeStructure()
                                 .toKripkeTruthTable(),
                         ktt -> ktt.toQBFString(Boolean.parseBoolean(upUntil), Integer.parseInt(steps)),
                         KripkeTruthTable::toString)
                .computeJSON();
    }

    @GetMapping("/simplify/{formula}")
    public String simplify(@PathVariable String formula) {
        return new Result(() -> Simplification.of(preprocess(formula))
                .toString())
                .computeJSON();
    }
}

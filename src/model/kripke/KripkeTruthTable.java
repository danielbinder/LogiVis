package model.kripke;

import servlet.Result;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KripkeTruthTable {
    /** Defines the order of literals */
    private final List<String> literals;
    /** Map<currentAssignment, List<possibleFutureAssignment>> */
    private final Map<Map<String, Boolean>, List<Map<String, Boolean>>> table = new HashMap<>();
    /** maxSuccessors = the amount of possible future Assignments */
    private final int maxSuccessors;
    private final List<Map<String, Boolean>> encodingStartVars = new ArrayList<>();
    private final List<Map<String, Boolean>> encodingEndVars = new ArrayList<>();

    public KripkeTruthTable(KripkeStructure ks) {
        literals = ks.get(0).stateMap.keySet().stream().toList();

        maxSuccessors = ks.stream()
                .map(kn -> kn.successors.size())
                .max(Integer::compareTo)
                .orElse(0);

        for(KripkeNode kn : ks) {
            if(kn.successors.size() > 0) {
                // Add the assignment of the current node as top-level key
                // Add the assignment of every successor as top-level value
                table.put(kn.stateMap, kn.successors.stream().map(succ -> succ.stateMap).collect(Collectors.toList()));

                // Pad the rest of the row with already existing assignments
                for(int i = 0; i < maxSuccessors - kn.successors.size(); i++)
                    table.get(kn.stateMap).add(table.get(kn.stateMap).get(0));
            }
        }

        ks.stream()
                .filter(kn -> kn.isEncodingStart)
                .forEach(kn -> encodingStartVars.add(kn.stateMap));
        ks.stream()
                .filter(kn -> kn.isEncodingEnd)
                .forEach(kn -> encodingEndVars.add(kn.stateMap));
    }

    public KripkeTruthTable(String kripkeStructure) {
        this(KripkeStructure.fromString(kripkeStructure));
    }

    public Result toFormulaStringWithEncodingStartAndEndAsResult(int steps) {
        return new Result(toFormulaStringWithEncodingStartAndEnd(steps), toString());
    }

    public String toFormulaStringWithEncodingStartAndEnd(int steps) {
        return toFormulaString(steps) + getEncodingStartAndEndString(steps);
    }

    public String toFormulaString(int steps) {
        return IntStream.range(0, steps)
                .mapToObj(i -> IntStream.range(0, maxSuccessors)
                        .mapToObj(futureAss -> literals.stream()
                                // if future assignment for current literal contains any true assignments
                                .map(literal -> table.keySet().stream().anyMatch(currAss -> table.get(currAss).get(futureAss).get(literal)) ?
                                        "(" + literal + (i + 1) + " <-> " + table.keySet().stream()
                                                .filter(currAss -> table.get(currAss).get(futureAss).get(literal))
                                                .map(currAss -> currAss.entrySet().stream()
                                                        .map(e -> (e.getValue() ? "" : "!") + e.getKey() + i)
                                                        .collect(Collectors.joining(" & ", "(", ")")))
                                                .collect(Collectors.joining(" | ", "(", ")")) + ")" :
                                        "!" + literal + (i + 1))
                                .collect(Collectors.joining(" & ", "(", ")")))
                        .collect(Collectors.joining(" |\n", "(", ")")))
                .collect(Collectors.joining(" &\n\n", "(", ")"));
    }

    public Result toQBFStringAsResult(int steps) {
        return new Result(toQBFString(steps), toString());
    }

    public String toQBFString(int steps) {
        return literals.stream()
                        .map(literal -> IntStream.range(0, steps)
                                .mapToObj(i -> "?" + literal + i)
                                .collect(Collectors.joining(" ")))
                        .collect(Collectors.joining(" ", "", "\n")) +
                literals.stream()
                        .map(literal -> "#" + literal + " #" + literal + "next")
                        .collect(Collectors.joining(" ", "", "\n")) +
                "(\n" +
                IntStream.range(0, steps)
                        .mapToObj(step -> literals.stream()
                                .map(literal -> "(" + literal + " <-> " + literal + step + ") & (" + literal + "next <-> " + literal + (step + 1) + ")")
                                .collect(Collectors.joining(" & ", "(", ")")))
                        .collect(Collectors.joining(" |\n", "(", ")\n")) +
                "->\n" +
                toFormulaString(1).replaceAll("(!?[a-z]+([a-z]*[0-9]*)*)0", "$1")
                        .replaceAll("(!?[a-z]+([a-z]*[0-9]*)*)1", "$1next") +
                "\n)" +
                getEncodingStartAndEndString(steps);
    }

    private String getEncodingStartAndEndString(int steps) {
        return (!encodingStartVars.isEmpty()
                        ? "\n& " +
                        encodingStartVars.stream()
                                .map(state -> state.entrySet().stream()
                                        .map(literal -> (literal.getValue() ? "" : "!") + literal.getKey() + "0")
                                        .collect(Collectors.joining(" & ", "(", ")")))
                                .collect(Collectors.joining(" | ", "(", ")"))
                        : "") +
                (!encodingEndVars.isEmpty()
                        ? " & " +
                        encodingEndVars.stream()
                                .map(state -> state.entrySet().stream()
                                        .map(literal -> (literal.getValue() ? "" : "!") + literal.getKey() + steps)
                                        .collect(Collectors.joining(" & ", "(", ")")))
                                .collect(Collectors.joining(" | ", "(", ")"))
                        : "");
    }

    @Override
    public String toString() {
        return String.join(" ", literals) +
                " || " +
                IntStream.range(0, maxSuccessors)
                        .mapToObj(i -> literals.stream()
                                .map(l -> l + "'")
                                .collect(Collectors.joining()))
                        .collect(Collectors.joining("| ")) +
                "\n" +
                table.entrySet().stream()
                        .map(entry -> literals.stream()
                                .map(l -> entry.getKey().get(l) ? "1" : "0")
                                .collect(Collectors.joining(" ")) +
                                " || " +
                                entry.getValue().stream()
                                        .map(value -> literals.stream()
                                                .map(l -> value.get(l) ? "1" : "0")
                                                .collect(Collectors.joining(" ")))
                                        .collect(Collectors.joining(" | ")))
                        .collect(Collectors.joining("\n"));
    }
}

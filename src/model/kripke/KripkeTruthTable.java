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
    }

    public KripkeTruthTable(String kripkeStructure) {
        this(KripkeStructure.fromString(kripkeStructure));
    }

    public Result toFormulaStringWithResult(int steps) {
        return new Result(toFormulaString(steps), toString());
    }

    public String toFormulaString(int steps) {
        return IntStream.range(0, steps)
                .mapToObj(i -> IntStream.range(0, maxSuccessors)
                        .mapToObj(futureAss -> literals.stream()
                                // if future assignement for current literal contains any true assignments
                                .map(literal -> table.keySet().stream().anyMatch(currAss -> table.get(currAss).get(futureAss).get(literal)) ?
                                        "(" + literal + (i + 1) + " <-> " + table.keySet().stream()
                                                .filter(currAss -> table.get(currAss).get(futureAss).get(literal))
                                                .map(currAss -> currAss.entrySet().stream()
                                                        .map(e -> (e.getValue() ? "" : "!") + e.getKey() + i)
                                                        .collect(Collectors.joining(" & ", "(", ")")))
                                                .collect(Collectors.joining(" | ", "(", ")")) + ")" :
                                        "!" + literal + (i + 1))
                                .collect(Collectors.joining(" & ", "(", ")")))
                        .collect(Collectors.joining(" | ", "(", ")")))
                .collect(Collectors.joining(" & ", "(", ")"));
    }

    public Result toQBFStringWithResult(int steps) {
        return new Result(toQBFString(steps), toString());
    }

    public String toQBFString(int steps) {
        final StringBuilder sb = new StringBuilder();

        final List<Integer> stepRange = IntStream.range(0, steps + 1).boxed().toList();
        final TreeSet<String> universalStateLiterals = new TreeSet<>();
        final TreeSet<String> existentialStateLiterals = new TreeSet<>();
        stepRange.forEach(step -> literals.forEach(literal -> {
                if(step == 0) {
                    existentialStateLiterals.add(literal);
                } else {
                    existentialStateLiterals.add(literal + "n" + step);
                }
                universalStateLiterals.add(literal + step);
            })
        );

        final StringBuilder quantifiedVariables = new StringBuilder();
        // existentially quantify state variable aliases
        existentialStateLiterals.forEach(literal -> quantifiedVariables.append("?").append(literal).append(" "));

        quantifiedVariables.append("\n");

        // universally quantify state variables
        universalStateLiterals.forEach(literal -> quantifiedVariables.append("#").append(literal).append(" "));

        quantifiedVariables.append("\n");
        sb.append(quantifiedVariables);
        sb.append("( ");

        final StringBuilder aliases = new StringBuilder();
        aliases.append("(");
        List<String> existentialAliases = new ArrayList<>(existentialStateLiterals);
        universalStateLiterals.forEach(literal -> aliases.append("(")
                .append(literal)
                .append(" <-> ")
                .append(existentialAliases.remove(0))
                .append(")")
                .append(" & "));
        aliases.setLength(aliases.length() - 3);
        aliases.append(")");

        sb.append(aliases);
        sb.append("\n  -> \n");
        sb.append(this.toFormulaString(steps));
        sb.append("\n)");
        return sb.toString();
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

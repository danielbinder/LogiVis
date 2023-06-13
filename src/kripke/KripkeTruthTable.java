package kripke;

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

    public String toFormulaString(int steps) {
        StringBuilder formula = new StringBuilder();

        for(int i = 0; i < steps; i++) {
            formula.append("(");

            for(int futureAss = 0; futureAss < maxSuccessors; futureAss++) {
                formula.append("(");

                for(String literal : literals) {
                    String stateLiteral = literal + (i + 1);
                    formula.append("(")
                            .append(stateLiteral)
                            .append(" <-> ((");

                    boolean noAssignment = true;
                    for(Map<String, Boolean> currAss : table.keySet()) {
                        if(table.get(currAss).get(futureAss).get(literal)) {
                            noAssignment = false;
                            int finalI = i;
                            formula.append(currAss.entrySet().stream()
                                                   .map(e -> (e.getValue() ? "" : "!") + e.getKey() + finalI)
                                                   .collect(Collectors.joining(" & ")))
                                    .append(") | (");
                        }
                    }

                    if(noAssignment) {
                        formula.setLength(formula.length() - 7 - stateLiteral.length());
                        formula.append("!")
                                .append(literal)
                                .append(i + 1)
                                .append(" & ");
                    } else {
                        formula.setLength(formula.length() - 4);
                        formula.append(")) & ");
                    }
                }

                formula.setLength(formula.length() - 3);
                formula.append(") |\n");
            }

            formula.setLength(formula.length() - 3);
            formula.append(")) &\n");
        }

        formula.setLength(formula.length() - 3);

        return formula.toString();
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
        StringBuilder result = new StringBuilder();

        result.append(String.join(" ", literals))
                .append(" || ");

        for(int i = 0; i < maxSuccessors; i++) {
            result.append(literals.stream().map(l -> l + "'").collect(Collectors.joining("")))
                    .append("| ");
        }

        result.append("\n");

        for(Map<String, Boolean> key : table.keySet()) {
            result.append(literals.stream().map(l -> key.get(l) ? "1" : "0").collect(Collectors.joining(" ")))
                    .append(" || ");

            for(Map<String, Boolean> value : table.get(key)) {
                result.append(literals.stream().map(l -> value.get(l) ? "1" : "0").collect(Collectors.joining(" ")))
                        .append(" | ");
            }

            result.append("\n");
        }

        return result.toString();
    }
}

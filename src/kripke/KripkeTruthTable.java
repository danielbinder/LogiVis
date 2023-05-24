package kripke;

import java.util.*;
import java.util.stream.Collectors;

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
            // Add the assignment of the current node as top-level key
            // Add the assignment of every successor as top-level value
            table.put(kn.stateMap, kn.successors.stream().map(succ -> succ.stateMap).collect(Collectors.toList()));

            // Pad the rest of the row with already existing assignments
            for(int i = 0; i < maxSuccessors - kn.successors.size(); i++)
                table.get(kn.stateMap).add(table.get(kn.stateMap).get(0));
        }
    }

    public String toFormulaString(int steps) {
        StringBuilder formula = new StringBuilder();

        for(int i = 0; i < steps; i++) {
            formula.append("(");

            for(int futureAss = 0; futureAss < maxSuccessors; futureAss++) {
                formula.append("(");

                for(String literal : literals) {
                    formula.append("(")
                            .append(literal)
                            .append(i + 1)
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
                        formula.setLength(formula.length() - 2);
                        formula.append("false) & ");
                    } else {
                        formula.setLength(formula.length() - 4);
                        formula.append(")) & ");
                    }
                }

                formula.setLength(formula.length() - 3);
                formula.append(") |\n");
            }

            formula.setLength(formula.length() - 3);
            formula.append(") &\n");
        }

        formula.setLength(formula.length() - 3);

        return formula.toString();
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

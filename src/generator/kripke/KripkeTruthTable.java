package generator.kripke;

import java.util.*;
import java.util.stream.Collectors;

public class KripkeTruthTable {
    private final List<String> literals;
    /** Map<currentAssignment, List<possibleFutureAssignment>> */
    private final Map<Map<String, Boolean>, List<Map<String, Boolean>>> table = new HashMap<>();

    public KripkeTruthTable(KripkeStructure ks) {
        literals = ks.get(0).stateMap.keySet().stream().toList();

        for(int i = 0; i < Math.pow(2, literals.size()); i++) {
            StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(i));

            // Add '0'-padding in front
            while(binaryString.length() < literals.size()) binaryString.insert(0, "0");

            table.put(assignmentMapFromAssignmentString(binaryString.toString()), new ArrayList<>());
        }

        int maxSuccessors = ks.stream()
                .map(kn -> kn.successors.size())
                .max(Integer::compareTo)
                .orElse(0);
        Map<String, Boolean> zeroLiterals = assignmentMapFromAssignmentString(
                literals.stream().map(l -> "0").collect(Collectors.joining()));

        for(KripkeNode kn : ks) {
            // Add the assignment of every successor
            for(KripkeNode succ : kn.successors)
                table.get(kn.stateMap).add(succ.stateMap);

            // Pad the rest of the row with zeros
            for(int i = 0; i < maxSuccessors - kn.successors.size(); i++)
                table.get(kn.stateMap).add(zeroLiterals);
        }

        // Fill any rows that haven't been filled
        for(Map<String, Boolean> key : table.keySet()) {
            if(table.get(key).isEmpty()) {
                for(int i = 0; i < maxSuccessors; i++) {
                    table.get(key).add(zeroLiterals);
                }
            }
        }

        System.out.println(this);
    }

    private Map<String, Boolean> assignmentMapFromAssignmentString(String assignmentString) {
        return literals.stream().collect(
                Collectors.toMap(l -> l,
                                 l -> assignmentString.charAt(literals.indexOf(l)) == '1'));
    }

    public String toFormulaString(int steps) {
        StringBuilder formula = new StringBuilder();

        for(int i = 0; i < steps; i++) {
            for(List<Map<String, Boolean>> list : table.values()) {
                formula.append("(");

                for(String literal : literals) {
                    formula.append("(")
                            .append(literal)
                            .append(i + 1)
                            .append(" <-> ((");

                    int amountFutureAssignments = 0;
                    for(Map<String, Boolean> futureAssignment : list) {
                        if(futureAssignment.get(literal)) {
                            amountFutureAssignments++;
                            int finalI = i;
                            formula.append(table.keySet()
                                                   .stream()
                                                   .filter(k -> table.get(k).equals(list))
                                                   .findAny().orElseThrow(IllegalStateException::new)
                                                   .entrySet().stream()
                                                   .map(e -> (e.getValue() ? "" : "!") + e.getKey() + finalI)
                                                   .collect(Collectors.joining(" & ")));

                            formula.append(") | (");
                        }
                    }

                    if(amountFutureAssignments == 0) formula.append("false)) & ");
                    else formula.append("false) & ");
                }
                formula.append("true)\n & ");
            }
            formula.append("true");
        }

        return formula.toString();
    }

    @Override
    public String toString() {
        String result = "";

        result += String.join(" ", literals);
        result += " || ";

        int maxSuccessors = table.values().stream()
                .findAny().orElseThrow(NoSuchElementException::new)
                .size();
        for(int i = 0; i < maxSuccessors; i++) {
            result += literals.stream().map(l -> l + "'").collect(Collectors.joining(""));
            result += "| ";
        }

        result += "\n";

        for(Map<String, Boolean> key : table.keySet()) {
            result += literals.stream().map(l -> key.get(l) ? "1" : "0").collect(Collectors.joining(" "));
            result += " || ";

            for(Map<String, Boolean> value : table.get(key)) {
                result += literals.stream().map(l -> value.get(l) ? "1" : "0").collect(Collectors.joining(" "));
                result += " | ";
            }

            result += "\n";
        }

        return result;
    }
}

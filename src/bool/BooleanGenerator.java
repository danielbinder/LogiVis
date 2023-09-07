package bool;

import marker.Generator;

import java.util.List;
import java.util.stream.IntStream;

public class BooleanGenerator implements Generator {
    private static final List<String> operationSymbols = List.of(" & ", " & ", " | ", " | ", " -> ", " <-> ");

    public static String generate(String variables, String operators) {
        int vars = Integer.parseInt(variables);
        if(vars < 1) Generator.error("The minimum amount of variables needs to be at least 1!");
        int ops = Integer.parseInt(operators);
        if(ops < vars - 1) Generator.error("The minimum amount of operators is " + (vars - 1) + " (variables - 1)");

        List<String> variableNames = generateVariableNames(vars);
        int additionalOps = ops - (vars - 1);
        int openParens = 0;

        StringBuilder formula = new StringBuilder();
        boolean endsWithOp = false;
        for(String varName : variableNames) {
            formula.append(switch(Generator.pickRandom(9)) {
                // To even make !-chance 50% and (-chance 50%:
                case 0, 6, 7, 8 -> "";
                case 1 -> additionalOps-- > 0 ? "!" : "";
                case 2 -> {
                    openParens++;
                    yield "(";
                }
                case 3 -> {
                    openParens++;
                    yield additionalOps-- > 0 ? "!(" : "(";
                }
                case 4 -> {
                    openParens++;
                    yield additionalOps-- > 0 ? "(!" : "(";
                }
                case 5 -> {
                    openParens++;
                    if(additionalOps > 1) {
                        additionalOps -= 2;
                        yield "!(!";
                    } else yield "(";
                }
                default -> throw new IllegalStateException("Unexpected value in BooleanGenerator!");
            });

            formula.append(varName);
            endsWithOp = false;

            if(openParens > 0 && Generator.pickRandom(2) == 0) {
                openParens--;
                formula.append(")");
            }

            // if(not last || additional variables available)
            if(!varName.equals(variableNames.get(variableNames.size() - 1))) {
                formula.append(operationSymbols.get(Generator.pickRandom(operationSymbols.size())));
                endsWithOp = true;
            }
        }

        variableNames = Generator.pickRandomRepeatable(variableNames.size(), additionalOps + 1).stream()
                .map(variableNames::get)
                .toList();

        int currVarName = 0;
        while(additionalOps > 0) {
            additionalOps--;
            formula.append(operationSymbols.get(Generator.pickRandom(operationSymbols.size())));

            formula.append(switch(Generator.pickRandom(9)) {
                // To even make !-chance 50% and (-chance 50%:
                case 0, 6, 7, 8 -> "";
                case 1 -> additionalOps-- > 0 ? "!" : "";
                case 2 -> {
                    openParens++;
                    yield "(";
                }
                case 3 -> {
                    openParens++;
                    yield additionalOps-- > 0 ? "!(" : "(";
                }
                case 4 -> {
                    openParens++;
                    yield additionalOps-- > 0 ? "(!" : "(";
                }
                case 5 -> {
                    openParens++;
                    if(additionalOps > 1) {
                        additionalOps -= 2;
                        yield "!(!";
                    } else yield "(";
                }
                default -> throw new IllegalStateException("Unexpected value in BooleanGenerator!");
            });

            formula.append(variableNames.get(currVarName++));
            endsWithOp = false;

            if(openParens > 0 && Generator.pickRandom(2) == 0) {
                openParens--;
                formula.append(")");
            }
        }

        if(endsWithOp) formula.append(variableNames.get(currVarName));
        while(openParens-- > 0) formula.append(")");

        return formula.toString();
    }

    private static List<String> generateVariableNames(int amount) {
        return IntStream.range(0, amount)
                .mapToObj(i -> ((char) ('a' + (i % 25))) + (amount > 26 ? String.valueOf(amount / 26) : ""))
                .toList();
    }
}

package ctl.generator;

import marker.Generator;

import java.util.List;
import java.util.stream.IntStream;

public class CTLGenerator implements Generator {
    private static final List<String> booleanOperationSymbols = List.of(" & ", " & ", " | ", " | ", " -> ", " <-> ");
    private static final List<String> temporalOperationSymbols = List.of("EX(", "AX(",
            "EF(", "AF(", "EG(", "AG(");
    private static final List<String> untilOperators = List.of("E(%s U %s)", "A(%s U %s)");

    public static String generate(String variables, String operators) {
        int vars = Integer.parseInt(variables);
        if(vars < 1) Generator.error("The minimum amount of variables needs to be at least 1!");
        int ops = Integer.parseInt(operators);
        if(ops < vars - 1) Generator.error("The minimum amount of operators is " + (vars - 1) + " (variables - 1)");

        List<String> variableNames = Generator.generateVariableNames(vars);
        int additionalOps = ops - (vars - 1);
        int openParens = 0;

        StringBuilder formula = new StringBuilder();
        boolean endsWithOp = false;
        boolean lastOpUntil = false;
        for(String varName : variableNames) {
            formula.append(switch((Integer) Generator.pickRandom(17)) {
                // To even make !-chance 50% and (-chance 50%:
                case Integer val when val == 0 || IntStream.range(11, 19).anyMatch(n -> n == val) -> "";
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
                case 6 -> {
                    openParens++;
                    yield pickRandomTemporalOperator();
                }
                case 7 -> {
                    openParens += 2;
                    yield additionalOps-- > 0 ? "!(" + pickRandomTemporalOperator()
                            : "(" + pickRandomTemporalOperator();
                }
                case 8 -> {
                    openParens += 2;
                    yield additionalOps-- > 0 ? "(!" + pickRandomTemporalOperator()
                            : "(" + pickRandomTemporalOperator();
                }
                case 9 -> {
                    if(additionalOps > 1) {
                        openParens += 2;
                        additionalOps -= 2;
                        yield "!(!" + pickRandomTemporalOperator();
                    } else {
                        openParens++;
                        yield "(" + pickRandomTemporalOperator();
                    }
                }
                case 10 -> {
                    additionalOps--;
                    lastOpUntil = true;
                    yield String.format(untilOperators.get(Generator.pickRandom(untilOperators.size())),
                            variableNames.get(Generator.pickRandom(variableNames.size())),
                            variableNames.get(Generator.pickRandom(variableNames.size())));
                }
                default -> throw new IllegalStateException("Unexpected value in CTLGenerator!");
            });

            if(lastOpUntil) {
                formula.append(booleanOperationSymbols.get(Generator.pickRandom(booleanOperationSymbols.size())));
                lastOpUntil = false;
            }

            formula.append(varName);
            endsWithOp = false;

            if (openParens > 0 && Generator.pickRandom(2) == 0) {
                openParens--;
                formula.append(")");
            }

            // if(not last)
            if (!varName.equals(variableNames.get(variableNames.size() - 1))) {
                formula.append(booleanOperationSymbols.get(Generator.pickRandom(booleanOperationSymbols.size())));
                endsWithOp = true;
            }
        }

        variableNames = Generator.pickRandomRepeatable(variableNames.size(), additionalOps + 1).stream()
                .map(variableNames::get)
                .toList();

        int currVarName = 0;
        while(additionalOps > 0) {
            additionalOps--;
            formula.append(booleanOperationSymbols.get(Generator.pickRandom(booleanOperationSymbols.size())));

            formula.append(switch((Integer) Generator.pickRandom(17)) {
                // To even make !-chance 50% and (-chance 50%:
                case Integer val when val == 0 || IntStream.range(10, 17).anyMatch(n -> n == val) -> "";
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
                case 6 -> {
                    openParens++;
                    yield pickRandomTemporalOperator();
                }
                case 7 -> {
                    openParens += 2;
                    yield additionalOps-- > 0 ? "!(" + pickRandomTemporalOperator()
                            : "(" + pickRandomTemporalOperator();
                }
                case 8 -> {
                    openParens += 2;
                    yield additionalOps-- > 0 ? "(!" + pickRandomTemporalOperator()
                            : "(" + pickRandomTemporalOperator();
                }
                case 9 -> {
                    if(additionalOps > 1) {
                        openParens += 2;
                        additionalOps -= 2;
                        yield "!(!" + pickRandomTemporalOperator();
                    } else {
                        openParens++;
                        yield "(" + pickRandomTemporalOperator();
                    }
                }
                default -> throw new IllegalStateException("Unexpected value in CTLGenerator!");
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

    private static String pickRandomTemporalOperator() {
        return temporalOperationSymbols.get(Generator.pickRandom(temporalOperationSymbols.size()));
    }
}
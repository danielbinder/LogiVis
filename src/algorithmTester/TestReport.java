package algorithmTester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestReport<I, E> {
    private static final String TICK = "vvv_tick";
    private static final String QMARK = "vvv_qmark";
    private static final String X = "vvv_x";
    private final Map<String, List<String>> testMap = new HashMap<>();
    private final BiFunction<E, E, Boolean> customEquals;

    public TestReport(BiFunction<E, E, Boolean> customEquals) {
        this.customEquals = customEquals;
    }

    public TestReport() {
        this(null);
    }

    public void test(String testName, Function<I, E> testFunction, Map<I, E> inputExpectedMap) {
        getFor(testName).addAll(inputExpectedMap.entrySet().stream()
                                .map(e -> equals(e.getKey().toString(), () -> testFunction.apply(e.getKey()), e.getValue()))
                                .toList());
    }

    public void test(String testName, BiFunction<I, I, E> testFunction, List<I> inputs1, List<I> inputs2, List<E> expectations) {
        getFor(testName).addAll(IntStream.range(0, inputs1.size())
                                        .mapToObj(i -> equals(inputs1.get(i) + "\n" + inputs2.get(i),
                                                              () -> testFunction.apply(inputs1.get(i), inputs2.get(i)),
                                                              expectations.get(i)))
                                        .toList());
    }

    public void testTrue(String testName, Function<I, Boolean> testFunction, List<I> inputs) {
        getFor(testName).addAll(inputs.stream()
                                        .map(input -> equalsBoolean(input.toString(),
                                                                    () -> testFunction.apply(input),
                                                                    true))
                                        .toList());
    }

    public void testTrue(String testName, BiFunction<I, I, Boolean> testFunction, List<I> inputs1, List<I> inputs2) {
        getFor(testName).addAll(IntStream.range(0, inputs1.size())
                                        .mapToObj(i -> equalsBoolean(inputs1 + "\n" + inputs2,
                                                                     () -> testFunction.apply(inputs1.get(i), inputs2.get(i)),
                                                                     true))
                                        .toList());
    }

    public void testFalse(String testName, Function<I, Boolean> testFunction, List<I> inputs) {
        getFor(testName).addAll(inputs.stream()
                                        .map(input -> equalsBoolean(input.toString(),
                                                                    () -> testFunction.apply(input),
                                                                    false))
                                        .toList());
    }

    public void testFalse(String testName, BiFunction<I, I, Boolean> testFunction, List<I> inputs1, List<I> inputs2) {
        getFor(testName).addAll(IntStream.range(0, inputs1.size())
                                        .mapToObj(i -> equalsBoolean(inputs1.get(i) + "\n" + inputs2.get(i),
                                                                     () -> testFunction.apply(inputs1.get(i), inputs2.get(i)),
                                                                     false))
                                        .toList());
    }

    public void compare(String testName, Function<I, E> testFunction, Function<I, E> sampleFunction, List<I> inputs) {
        getFor(testName).addAll(inputs.stream()
                                        .map(input -> equals(input.toString(),
                                                             () -> testFunction.apply(input),
                                                             () -> sampleFunction.apply(input)))
                                        .toList());
    }

    public void compare(String testName, BiFunction<I, I, E> testFunction, BiFunction<I, I, E> sampleFunction, List<I> inputs1, List<I> inputs2) {
        getFor(testName).addAll(IntStream.range(0, inputs1.size())
                                        .mapToObj(i -> equals(inputs1.get(i) + "\n" + inputs2.get(i),
                                                              () -> testFunction.apply(inputs1.get(i), inputs2.get(i)),
                                                              () -> sampleFunction.apply(inputs1.get(i), inputs2.get(i))))
                                        .toList());
    }

    public void compareBoolean(String testName, Function<I, Boolean> testFunction, Function<I, Boolean> sampleFunction, List<I> inputs) {
        getFor(testName).addAll(inputs.stream()
                                        .map(input -> equalsBoolean(input.toString(),
                                                                    () -> testFunction.apply(input),
                                                                    () -> sampleFunction.apply(input)))
                                        .toList());
    }

    public void compareBoolean(String testName, BiFunction<I, I, Boolean> testFunction, BiFunction<I, I, Boolean> sampleFunction, List<I> inputs1, List<I> inputs2) {
        getFor(testName).addAll(IntStream.range(0, inputs1.size())
                                        .mapToObj(i -> equalsBoolean(inputs1.get(i) + "\n" + inputs2.get(i),
                                                                     () -> testFunction.apply(inputs1.get(i), inputs2.get(i)),
                                                                     () -> sampleFunction.apply(inputs1.get(i), inputs2.get(i))))
                                        .toList());
    }

    private String equalsBoolean(String input, Callable<Boolean> callable, boolean expected) {
        try {
            return callable.call().equals(expected)
                    ? success(input)
                    : failure(input, String.valueOf(!expected), String.valueOf(expected));
        } catch(Exception e) {
            return failure(input, e.toString(), String.valueOf(expected));
        }
    }

    private String equalsBoolean(String input, Callable<Boolean> callable, Callable<Boolean> expectedCallable) {
        boolean expected;
        boolean test;
        try {
            expected = expectedCallable.call();
        } catch(Exception e) {
            try {
                test = callable.call();
            } catch(Exception e2) {
                return success(input);
            }

            return failure(input, e.toString(), String.valueOf(test));
        }

        try {
            test = callable.call();
        } catch(Exception e) {
            return failure(input, e.toString(), String.valueOf(expected));
        }

        return equalsBoolean(input, test, expected);
    }

    private String equals(String input, Callable<E> callable, E expected) {
        try {
            return equals(input, callable.call(), expected);
        } catch(Exception e) {
            return failure(input, e.toString(), expected.toString());
        }
    }

    private String equals(String input, Callable<E> callable, Callable<E> expectedCallable) {
        E expected;
        E test;
        try {
            expected = expectedCallable.call();
        } catch(Exception e) {
            try {
                test = callable.call();
            } catch(Exception e2) {
                return success(input);
            }

            return failure(input, e.toString(), test.toString());
        }

        try {
            test = callable.call();
        } catch(Exception e) {
            return failure(input, e.toString(), expected.toString());
        }

        return equals(input, test, expected);
    }

    private String equalsBoolean(String input, boolean test, boolean expected) {
        return test == expected
                ? success(input)
                : failure(input, String.valueOf(test), String.valueOf(expected));
    }

    private String equals(String input, E test, E expected) {
        if(customEquals == null) {
            return test.equals(expected)
                    ? success(input)
                    : failure(input, test.toString(), expected.toString());
        }

        try {
            return customEquals.apply(test, expected)
                    ? success(input)
                    : failure(input, test.toString(), expected.toString());
        } catch(Exception e) {
            return uncertain(input);
        }
    }

    public String compile(String name, boolean compact) {
        String result = testMap.entrySet().stream()
                .map(e -> {
                    String test = e.getValue().stream()
                            .map(String::toString)
                            .filter(s -> !compact || !s.contains(TICK))
                            .collect(Collectors.joining("\n"));
                    if(test.contains(X)) return X + " " + e.getKey() +
                            (compact
                                    ? " " + (TICK + " ").repeat((int) e.getValue().stream().filter(s -> s.contains(TICK)).count())
                                    : "") +
                            "\n" + test;
                    else if(test.contains(QMARK)) return QMARK + " " + e.getKey() +
                            (compact
                                    ? " " + (TICK + " ").repeat((int) e.getValue().stream().filter(s -> s.contains(TICK)).count())
                                    : "") +
                            "\n" + test;
                    else if(compact) return TICK + " " + e.getKey() + " " + (TICK + " ").repeat((int) e.getValue().stream().filter(s -> s.contains(TICK)).count());
                    else return TICK + " " + e.getKey() + "\n" + test;
                })
                .collect(Collectors.joining("\n"));

        return "Verdict" + (!name.isBlank() ? " for " + name : "") + ":" + verdict(result) + "\n\n" + result;
    }

    private String verdict(String result) {
        return "  " + TICK + " " + count(result, TICK) +
                "  " + QMARK + " " + count(result, QMARK) +
                "  " + X + " " + count(result, X);
    }

    private int count(String string, String subString) {
        return string.split(subString).length - 1;
    }

    public void sectionDivider(String testName) {
        getFor(testName).add("\t---");
    }

    public void success(String testName, String s) {
        getFor(testName).add(success(s));
    }

    private String success(String input) {
        return "\t" + TICK + " " + input.replace("\n", "\n\t   ");
    }

    public void uncertain(String testName, String s) {
        getFor(testName).add(uncertain(s));
    }

    private String uncertain(String name) {
        return "\t" + QMARK + " " + name.replace("\n", "\n\t   ");
    }

    public void failure(String testName, String input, String test, String expected) {
        getFor(testName).add(failure(input, test, expected));
    }

    private String failure(String input, String test, String expected) {
        return "\t" + X + " " + input.replace("\n", "\n\t   ") + "\n" +
                "\t\tExpected:\n\t\t\t" + expected.replace("\n", "\n\t\t\t") + "\n" +
                "\t\tBut got:\n\t\t\t" + test.replace("\n", "\n\t\t\t");
    }

    private String failure(String input, String test, Exception e) {
        return  failure(input, test, e.toString());
    }

    private List<String> getFor(String testName) {
        return testMap.computeIfAbsent(testName, s -> new ArrayList<>());
    }
}

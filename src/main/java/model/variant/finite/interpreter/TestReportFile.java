package model.variant.finite.interpreter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static model.variant.finite.interpreter.TestReport.*;
import static util.Hashing.sha512;

public class TestReportFile {
    private static final List<Pattern> METHOD_PATTERNS =
            List.of(Pattern.compile("public boolean isDeterministic\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public boolean isComplete\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public boolean isEquivalent\\(FiniteAutomaton automaton1, FiniteAutomaton automaton2\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public boolean isSimulatedBy\\(FiniteAutomaton automaton1, FiniteAutomaton automaton2\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public boolean areReachable\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public FiniteAutomaton toProductAutomaton\\(FiniteAutomaton automaton1, FiniteAutomaton automaton2\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public FiniteAutomaton toPowerAutomaton\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public FiniteAutomaton toComplementAutomaton\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public FiniteAutomaton toSinkAutomaton\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public FiniteAutomaton toOracleAutomaton\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    Pattern.compile("public FiniteAutomaton toOptimisedOracleAutomaton\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s+@Override", Pattern.DOTALL),
                    // End last pattern in file with \\z, since there is no @Override after
                    Pattern.compile("public Set<Set<State>> getStronglyConnectedComponents\\(FiniteAutomaton automaton\\) \\{(.*?)}\\s*\\z", Pattern.DOTALL));
    private static final Set<String> KNOWN_WORDS =
            Set.of("if", "return", "stream", "getInitialStates", "size", "allMatch", "toCheck",
                   "getSuccessorProperties", "getSuccessorsFor", "map", "FiniteAutomaton", "getAlphabet", "noneMatch",
                   "equals", "automaton1", "automaton2", "clone", "isEmpty", "getFinalStates", "Pair", "findAny",
                   "orElseThrow", "NoSuchElementException", "new", "true", "false", "HashSet", "State", "contains",
                   "left", "right", "forEach", "getOrCreate", "get", "combinedName", "isInitialState", "isFinalState",
                   "addSuccessor", "name", "filter", "Set", "String", "addAll", "for", "automaton", "throw",
                   "else", "isComplete", "isDeterministic", "toSinkAutomaton", "toPowerAutomaton", "int", "boolean");

    public static void compile(String tests, String name) {
        String timeStamp = LocalDateTime.now().toString();

        try {
            String report = timeStamp + "\n"
                    + replaceCharacters(tests) + "\n\n"
                    + "hashed report list:\n"
                    + hash(name, timeStamp, tests);

            Files.createDirectories(Path.of(System.getProperty("user.dir") + "/resources/"));
            Files.writeString(Path.of(System.getProperty("user.dir") + "/resources/" + name + ".report"),
                    report,
                    StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hash(String name, String timeStamp, String tests) {
        String implementation = "";
        try {
            implementation = Files.readString(Path.of(System.getProperty("user.dir") + "/src/main.java.algorithmTester/YourImplementation.java"));
        } catch(IOException ignored) {}

        String finalImplementation = implementation;
        return "[\n" + METHOD_PATTERNS.stream()
                .map(p -> {
                    Matcher m = p.matcher(finalImplementation);
                    if(m.find()) {
                        String method = m.group(1).replaceAll("//.*", "")
                                .replaceAll("\\s", " ")
                                .replaceAll("[a-zA-Z]*Exception\\(.*?\\);", "");
                        return "\t{\n" +
                                // author
                                "\t\t\"author\":\"" + name + "\",\n" +
                                //timestamp
                                "\t\t\"timestamp\":\"" + timeStamp + "\",\n" +
                                // method name
                                "\t\t\"mn\":\"" + p.pattern().replaceAll("public (boolean|FiniteAutomaton) ([a-zA-Z]+).*", "$2") + "\",\n" +
                                // author
                                "\t\t\"a\":\"" + sha512(name) + "\",\n" +
                                // timestamp
                                "\t\t\"ts\":\"" + sha512(timeStamp) + "\",\n" +
                                // Username (on local PC)
                                "\t\t\"un\":\"" + sha512(System.getProperty("user.name")) + "\",\n" +
                                // verdict
                                "\t\t\"v\":\"" + sha512(replaceCharacters(tests.split("\n")[0])) + "\",\n" +
                                // raw content
                                "\t\t\"rc\":\"" + sha512(method) + "\",\n" +
                                // no whitespaces
                                "\t\t\"nw\":\"" + sha512(method.replaceAll("\\s", "")) + "\",\n" +
                                // symbolised content; no whitespaces
                                "\t\t\"scnw\":\"" + sha512(replaceUnknownWords(method).replaceAll("\\s", "")) + "\",\n" +
                                // unknown words i.e. variable names
                                "\t\t\"uw\":\"" + sha512(getUnknownWords(method).stream()
                                                            .sorted()
                                                            .collect(Collectors.joining(""))) + "\"\n" +
                                "\t}";
                    }

                    return "\t{\n\t}";
                })
                .collect(Collectors.joining(",\n")) + "\n]";
    }

    private static Set<String> getUnknownWords(String method) {
        return Arrays.stream(method.split("\\s|[<>=\\-&|().;10!\"{},+:]"))
                .filter(w -> KNOWN_WORDS.stream().noneMatch(w::contains))
                .collect(Collectors.toSet());
    }

    private static String replaceUnknownWords(String method) {
        String result = method;
        for(String word : getUnknownWords(method)) result = result.replace(word, "");

        return result;
    }

    private static String replaceCharacters(String s) {
        return s.replace(TICK, "\u2714")
                .replace(X, "\u274C")
                .replace(QMARK, "\u2754");
    }
}

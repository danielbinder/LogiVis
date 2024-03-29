package util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static util.Logger.stdOut;

public class Result {
    //In DEV mode, stacktraces are printed to the console. Don't use in production!
    public static boolean DEV = false;

    public String result = "";
    public String info = "";
    public String warning = "";
    public String error = "";


    private Result(String result, String info, String warning, String error) {
        this.result = result;
        this.info = info;
        this.warning = warning;
        this.error = error;
    }

    public Result(Supplier<String> resultSupplier) {
        this(resultSupplier, () -> "");
    }

    public Result(Supplier<String> resultSupplier, Supplier<String> infoSupplier) {
        this(resultSupplier, s -> s, s -> infoSupplier.get());
    }

    public Result(Supplier<String> baseSupplier, Supplier<String> infoSupplier, Map<Predicate<String>, String> alternatives) {
        this(baseSupplier,
             base -> alternatives.keySet().stream()
                    .filter(p -> p.test(base))
                    .map(alternatives::get)
                    .findFirst()
                    .orElse(base),
             base -> infoSupplier.get());
    }

    public <T> Result(Supplier<T> baseSupplier, Function<T, List<Map<String, Boolean>>> resultFunction,
                      Function<T, String> infoFunction, Map<Predicate<T>, String> alternatives) {
        this(baseSupplier,
             base -> {
                List<Map<String, Boolean>> result = resultFunction.apply(base);
                return alternatives.keySet().stream()
                         .filter(p -> p.test(base))
                         .map(alternatives::get)
                         .findFirst()
                         .orElse(JSONof(result));
             },
             infoFunction);
    }

    public <T> Result(Supplier<T> baseSupplier, Function<T, String> resultFunction, Function<T, String> infoFunction) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        try {
            T base = baseSupplier.get();
            result = resultFunction.apply(base);
            info = infoFunction.apply(base);
            warning = outputStream.toString().replaceAll("\r", Matcher.quoteReplacement(""));
        } catch(Exception e) {
            warning = outputStream.toString().replaceAll("\r", Matcher.quoteReplacement(""));
            error = e.getMessage();
            if(DEV) e.printStackTrace(stdOut);        // For local debugging only!
        } finally {
            System.setOut(stdOut);
        }
    }

    public String computeJSON() {
        return "{\n" +
                // '$' has special meaning in Regex, Matcher.quoteReplacement() ignores that special meaning
                "\t\"result\": \"" + replaceIllegalCharacters(result).replaceAll("\n", Matcher.quoteReplacement("$")) + "\",\n" +
                "\t\"info\": \"" + replaceIllegalCharacters(info).replaceAll("\n", Matcher.quoteReplacement("$")) + "\",\n" +
                "\t\"warning\": \"" + replaceIllegalCharacters(warning).replaceAll("\n", Matcher.quoteReplacement("$")) + "\",\n" +
                "\t\"error\": \"" +replaceIllegalCharacters(error).replaceAll("\n", Matcher.quoteReplacement("$")) + "\"\n" +
                "}";
    }

    private static String replaceIllegalCharacters(String s) {
        return s.replace("\\", "\\\\")
                .replace("vvv_qmark", "\\u2754")
                .replace("vvv_x", "\\u274C")
                .replace("vvv_tick", "\\u2714")
                .replace("\t", "    ");
    }

    private static String JSONof(List<Map<String, Boolean>> listOfMap) {
        return listOfMap.stream()
                .map(Result::JSONof)
                .collect(Collectors.joining("$"));
    }

    private static String JSONof(Map<String, Boolean> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())     // Orders keys alphabetically
                .map(e -> (e.getValue() ? "" : "!") + e.getKey())
                .collect(Collectors.joining(", "));
    }

    /**
     * Careful! Don't use for sending over REST!!!
     * Use computeJSON() instead!
     * @return string representation for debugging
     * @deprecated This is only for debugging!
     */
    @Override
    @Deprecated
    public String toString() {
        return "Result{" +
                "result='" + result + "'\n" +
                ", info='" + info + "'\n" +
                ", warning='" + warning + "'\n" +
                ", error='" + error + "'\n" +
                '}';
    }
}

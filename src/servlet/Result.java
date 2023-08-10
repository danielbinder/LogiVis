package servlet;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class Result {
    public final String result;
    public final String info;
    public final String warning;
    public final String error;


    public Result(String result, String info, String warning, String error) {
        this.result = result;
        this.info = info;
        this.warning = warning;
        this.error = error;
    }

    public Result(String result) {
        this(result, "", "", "");
    }

    public Result(String result, String info) {
        this(result, info, "", "");
    }

    public Result(Map<String, Boolean> result) {
        this(result, "", "", "");
    }

    public Result(Map<String, Boolean> result, String info) { this(result, info, "", ""); }

    public Result(Map<String, Boolean> result, String info, String warning, String error) {
        this(JSONof(result), info, warning, error);
    }

    public Result(List<Map<String, Boolean>> result) {
        this(result, "", "", "");
    }

    public Result(List<Map<String, Boolean>> result, String info, String warning, String error) {
        this(JSONof(result), info, warning, error);
    }

    public Result result(String result) {
        return new Result(result, info, warning, error);
    }

    public Result info(String info) {
        return new Result(result, info, warning, error);
    }

    public Result warning(String warning) {
        return new Result(result, info, warning, error);
    }

    public Result error(String error) {
        return new Result(result, info, warning, error);
    }

    public String computeJSON() {
        return "{\n" +
                // '$' has special meaning in Regex, Matcher.quoteReplacement() ignores that special meaning
                "\t\"result\": \"" + result.replaceAll("\n", Matcher.quoteReplacement("$")) + "\",\n" +
                "\t\"info\": \"" + info.replaceAll("\n", Matcher.quoteReplacement("$")) + "\",\n" +
                "\t\"warning\": \"" + warning + "\",\n" +
                "\t\"error\": \"" + error + "\"\n" +
                "}";
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

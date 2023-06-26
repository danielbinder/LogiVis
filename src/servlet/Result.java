package servlet;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Result {
    private final String result;
    private final String info;
    private final String warning;
    private final String error;


    public Result(String result, String info, String warning, String error) {
        this.result = result;
        this.info = info;
        this.warning = warning;
        this.error = error;
    }

    public Result(String result) {
        this(result, "", "", "");
    }

    public Result(Map<String, Boolean> result) {
        this(result, "", "", "");
    }

    public Result(Map<String, Boolean> result, String info, String warning, String error) {
        this(JSONof(result), info, warning, error);
    }

    public Result(List<Map<String, Boolean>> result) {
        this(result, "", "", "");
    }

    public Result(List<Map<String, Boolean>> result, String info, String warning, String error) {
        this(JSONof(result), info, warning, error);
    }

    public String computeJSON() {
        return "{\n" +
                "\t\"result\": \"" + result.replaceAll("\n", "$") + "\",\n" +
                "\t\"info\": \"" + info.replaceAll("\n", "$") + "\",\n" +
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
                .collect(Collectors.joining(" "));
    }
}

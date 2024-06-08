package util;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Logger {
    public static final PrintStream STD_OUT = System.out;
    public static boolean SAVE_LOG = false;
    public static int LOG_LEVEL = 0;
    private static String SAVED_LOG = "";
    private static final PrintStream ORIGINAL_STD_OUT = System.out;
    private static final List<Function<String, String>> highlighters = List.of(
            (s) -> "\033[32m" + s + "\033[0m",
            (s) -> "\033[35m" + s + "\033[0m",
            (s) -> "\033[36m" + s + "\033[0m"
    );
    private static int highlighterIndex = 0;

    public static void maxLevel() {
        LOG_LEVEL = 100;
    }

    public static void minLevel() {
        LOG_LEVEL = 0;
    }

    public static void info(int level, String info, String...highlights) {
        info(level, highlight(Identifier.INFO, info, highlights));
    }

    public static void info(int level, String info) {
        if(LOG_LEVEL >= level) info(info);
    }

    public static void info(String info, String...highlights) {
        info(0, highlight(Identifier.INFO, info, highlights));
    }

    public static void info(String info) {
        if(SAVE_LOG) SAVED_LOG += transformLog(Identifier.INFO, info);
        STD_OUT.println(transformLogConsole(Identifier.INFO, info));
    }

    public static void warning(int level, String warning, String...highlights) {
        warning(level, highlight(Identifier.WARNING, warning, highlights));
    }

    public static void warning(int level, String warning) {
        if(LOG_LEVEL >= level) warning(warning);
    }

    public static void warning(String warning, String...highlights) {
        warning(0, highlight(Identifier.WARNING, warning, highlights));
    }

    public static void warning(String warning) {
        if(SAVE_LOG) SAVED_LOG += transformLog(Identifier.WARNING, warning);
        STD_OUT.println(transformLogConsole(Identifier.WARNING, warning));
    }

    public static void error(String error) {
        if(SAVE_LOG) SAVED_LOG += transformLog(Identifier.ERROR, error);
        STD_OUT.println(transformLogConsole(Identifier.ERROR, error));
    }

    public static void error(Exception e) {
        if(SAVE_LOG) {
            SAVED_LOG += "[ERROR] " + e.getMessage() + "\n" +
                    Arrays.stream(e.getStackTrace())
                            .map(StackTraceElement::toString)
                            .map(s -> "[ERROR] " + s)
                            .collect(Collectors.joining("\n")) + "\n";
        }
        STD_OUT.println(transformLogConsole(Identifier.ERROR, e.getMessage()));
        e.printStackTrace(STD_OUT);
    }

    public static String consumeSavedLog() {
        String savedLogTemp = SAVED_LOG;
        SAVED_LOG = "";
        return savedLogTemp;
    }

    private static String transformLogConsole(Identifier identifier, String logMessage) {
        return Arrays.stream(logMessage.split("\n"))
                .map(line -> STD_OUT == ORIGINAL_STD_OUT
                        ? identifier.transformLineConsole(line)
                        : identifier.transformLine(line))
                .collect(Collectors.joining("\n"));
    }

    private static String transformLog(Identifier identifier, String logMessage) {
        return Arrays.stream(logMessage.split("\n"))
                .map(identifier::transformLine)
                .collect(Collectors.joining("\n", "", "\n"));
    }

    private static String highlight(Identifier identifier, String text, String... highlights) {
        for(String highlight : highlights) {
            text = text.replaceAll(Pattern.quote(highlight), highlight(identifier, highlight));
        }

        return text;
    }

    private static String highlight(Identifier identifier, String highlight) {
        if (STD_OUT != ORIGINAL_STD_OUT) return highlight;

        String result = highlighters.get(highlighterIndex++).apply(highlight) + identifier.startColor;
        if (highlighterIndex >= highlighters.size()) highlighterIndex = 0;
        return result;
    }

    private enum Identifier {
        INFO("[INFO] ", ""),
        WARNING("[WARNING] ", "\033[33m"),
        ERROR("[ERROR] ", "\033[31m");

        private static final String RESET_COLOR = "\033[0m";

        private final String text;
        private final String startColor;

        Identifier(String text, String startColor) {
            this.text = text;
            this.startColor = startColor;
        }

        private String transformLineConsole(String line) {
            return startColor + transformLine(line) + RESET_COLOR;
        }

        private String transformLine(String line) {
            return text + line;
        }
    }
}

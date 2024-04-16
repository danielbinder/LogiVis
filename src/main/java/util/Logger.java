package util;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Logger {
    public static final PrintStream STD_OUT = System.out;
    public static boolean SAVE_LOG = false;
    private static String SAVED_LOG = "";

    public static void info(String info) {
        if(SAVE_LOG) SAVED_LOG += "[INFO] " + info + "\n";
        STD_OUT.println("[INFO] " + info);
    }

    public static void warning(String warning) {
        if(SAVE_LOG) SAVED_LOG += "[WARNING] " + warning + "\n";
        STD_OUT.println("[WARNING] " + warning);
    }

    public static void error(String error) {
        if(SAVE_LOG) SAVED_LOG += "[ERROR] " + error + "\n";
        STD_OUT.println("[ERROR] " + error);
    }

    public static void error(Exception e) {
        if(SAVE_LOG) {
            SAVED_LOG += "[ERROR] " + e.getMessage() + "\n" +
                    Arrays.stream(e.getStackTrace())
                            .map(StackTraceElement::toString)
                            .map(s -> "[ERROR] " + s)
                            .collect(Collectors.joining("\n")) + "\n";
        }
        STD_OUT.println("[ERROR] " + e.getMessage());
        e.printStackTrace(STD_OUT);
    }

    public static String consumeSavedLog() {
        String savedLogTemp = SAVED_LOG;
        SAVED_LOG = "";
        return savedLogTemp;
    }
}

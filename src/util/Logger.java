package util;

import java.io.PrintStream;

public class Logger {
    public static final PrintStream stdOut = System.out;

    public static void info(String info) {
        stdOut.println("[INFO] " + info);
    }

    public static void warning(String warning) {
        stdOut.println("[WARNING] " + warning);
    }

    public static void error(String error) {
        stdOut.println("[ERROR] " + error);
    }

    public static void error(Exception e) {
        stdOut.println("[ERROR] " + e.getMessage());
        e.printStackTrace(stdOut);
    }
}

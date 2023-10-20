package util;

import algorithmTester.AlgorithmTester;
import main.Frontend;
import main.FrontendExtractor;
import main.Main;
import servlet.Result;
import servlet.Servlet;
import servlet.rest.REST;

import java.util.Arrays;
import java.util.List;

public enum Args {
    RUN("run", "Runs the Servlet, AlgorithmTester and the pre-built front-end", true, Main::run),
    RUN_BACKEND("runBackend", "Runs the Servlet and AlgorithmTester", true, () -> {
        Servlet.run();
        AlgorithmTester.run();
    }),
    RUN_SERVLET("runServlet", "Runs the Servlet", true, Servlet::run),
    RUN_ALGORITHM_TESTER("runAlgorithmTester", "Runs the AlgorithmTester", true, AlgorithmTester::run),
    RUN_FRONTEND("runFrontend", "Runs the Frontend", true, Frontend::run),
    DEV("dev", "Prints stack traces of exceptions to the console", false, () -> Result.DEV = true),
    EXTRACT("extract", "Extracts the front-end out of the .jar file for execution", false, FrontendExtractor::extract),
    HELP("help", "Prints all available arguments to the console", false, Args::help);

    private final String argText;
    private final String description;
    private final boolean requiresSpark;
    private final Runnable task;

    Args(String argText, String description, boolean requiresSpark, Runnable task) {
        this.argText = argText;
        this.description = description;
        this.requiresSpark = requiresSpark;
        this.task = task;
    }

    public static void process(String[] args) {
        List<String> argList = Arrays.asList(args);
        List<String> possibleArgs = Arrays.stream(values())
                .map(arg -> arg.argText)
                .toList();

        argList.stream()
                .filter(arg -> !possibleArgs.contains(arg))
                .forEach(arg -> System.out.println("[WARNING] Unknown argument: " + arg));
        
        if((argList.contains("run") || argList.contains("runFrontend")) && !argList.contains("extract"))
            System.out.println("[WARNING] If you're running this from the .jar, you should add '-extract' to your arguments, which is required for the frontend to run");

        if(Arrays.stream(values())
                .filter(v -> argList.contains(v.argText))
                .anyMatch(arg -> arg.requiresSpark)) REST.setup();

        Arrays.stream(values())
                .filter(v -> argList.contains(v.argText))
                .map(v -> v.task)
                .forEach(Runnable::run);
    }

    private static void help() {
        System.out.println("Available arguments: ");

        Arrays.stream(values())
                .map(v -> v.argText + "\n\t" + v.description)
                .forEach(System.out::println);
    }
}
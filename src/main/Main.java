package main;

import algorithmTester.AlgorithmTester;
import servlet.Servlet;
import util.Args;

public class Main {
    public static void main(String[] args) {
        Args.process(args);
    }

    public static void run() {
        Frontend.run();
        Servlet.run();
        AlgorithmTester.run();
    }
}

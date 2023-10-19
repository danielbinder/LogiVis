package main;

import algorithmTester.AlgorithmTester;
import servlet.Servlet;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0 && FrontendExtractor.CLI_ARG_EXTRACT.equals(args[0])) FrontendExtractor.extract();
        Servlet.main(args);
        AlgorithmTester.main(args);
    }
}

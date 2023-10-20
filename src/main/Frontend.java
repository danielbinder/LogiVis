package main;

import spark.Spark;

import java.io.File;

public class Frontend {
    public static void run() {
        Spark.staticFiles.externalLocation(getStaticFileLocation());
        System.out.println("[INFO] Started Frontend. Go to: http://localhost:4000");
    }

    private static String getStaticFileLocation() {
        return System.getProperty("user.dir") + File.separator + "visualisation" + File.separator + "build";
    }
}

package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class FrontendExtractor {
    // static frontend files/directories which are the result of a production build
    // (every entry is mapped to a boolean value indicating if the target file is a directory (true) or not (false))
    private static final Map<String, Boolean> frontendFiles = Map.of(
            "asset-manifest.json", false,
            "index.html", false,
            "LogiVis.png", false,
            "manifest.json", false,
            "robots.txt", false,
            "static/", true
    );

    // target location of frontend folder
    private static final String TARGET_LOCATION = System.getProperty("user.dir")
            + File.separator + "visualisation"
            + File.separator + "build";

    public static void extract() {
        final String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if(!jarPath.isEmpty()) {
            try (JarFile jar = new JarFile(jarPath)) {
                initFrontendFolder();
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if (frontendFiles.containsKey(entry.getName())) { collectFrontendResources(jar, entry); }
                    else {
                        final List<String> targetDirectories = frontendFiles.entrySet().stream()
                                .filter(e -> Boolean.TRUE.equals(e.getValue()))
                                .map(Map.Entry::getKey).toList();
                        for(String dir : targetDirectories) {
                            if(entry.getName().startsWith(dir)) collectFrontendResources(jar, entry);
                        }
                    }
                }
            } catch (IOException ex) {
                System.err.println("Error while extracting JAR contents (" + ex.getMessage() + ").");
                ex.printStackTrace();
            }
        } else {
            System.out.println("Could not acquire path of JAR file.");
        }
    }

    private static void initFrontendFolder() throws IOException {
        final File target = new File(TARGET_LOCATION);
        if (target.exists()) deleteRecursively(target);
        if (target.mkdirs()) System.out.println("Created temporary frontend folder.");
        installShutdownHook();
    }

    private static void deleteRecursively(File f) throws IOException {
        try (Stream<Path> pathStream = Files.walk(f.toPath())) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        }
    }

    private static void collectFrontendResources(JarFile jar, JarEntry entry) throws IOException {
        final File target = new File(TARGET_LOCATION + File.separator + entry.getName());
        if(entry.isDirectory()) { Files.createDirectories(Paths.get(target.toURI())); }
        else {
            try(InputStream is = jar.getInputStream(entry)) {
                Files.copy(is, Paths.get(target.toURI()));
            }
        }
        System.out.println("Copied files: " + target.getPath());
    }

    private static void installShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                final File file = new File(TARGET_LOCATION);
                if (file.exists()) deleteRecursively(file.getParentFile());
            } catch (IOException ex) {
                System.err.println("Could not remove temporary frontend folder.");
            }
        }));
    }
}

package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileHelper {
    public static String read(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch(IOException e) {
            return null;
        }
    }

    public static List<Path> readAll(String folderPath) throws IOException {
        return Files.list(Path.of(folderPath))
                .toList();
    }

    public static void write(String path, String content) {
        try {
            Path file = Path.of(path);
            Files.createDirectories(file.getParent());
            Files.writeString(Files.createFile(file), content);
        } catch(IOException ignored) {}
    }
}

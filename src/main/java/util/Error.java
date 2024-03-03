package util;

public class Error {
    public static void printPosition(String line, int col) {
        if(line.length() > 40) {
            line = line.substring(Math.max(0, col - 40), Math.min(line.length(), col + 40));
            col = col - 40 < 0 ? col : 40;
        }

        System.out.println(line);
        System.out.println(" ".repeat(col - 1) + "^");
    }
}

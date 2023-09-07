import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import servlet.Servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServletTest {
    @BeforeAll
    public static void prepareServlet() {
        Servlet.main(new String[]{});
    }

    @Test
    public void testSolve() {
        testResult("a", "http://localhost:4000/solve/a");
    }

    @Test
    public void testParenthesise() {
        testResult("a & (b | c)", "http://localhost:4000/parenthesise/(a&(b|c))");
    }

    private void testResult(String expected, String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            try(Scanner s = new Scanner(connection.getInputStream())) {
                s.nextLine();   // Read over initial {\n of JSON

                assertEquals("\t\"result\": \"" + expected + "\",", s.nextLine());
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}

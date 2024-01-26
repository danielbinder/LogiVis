import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import main.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class ServletTest {
    @BeforeAll
    public static void prepareServlet() {
        Servlet.run();
    }

    @Test
    public void testSolve() {
        testResult("a", "http://localhost:4000/solve/a");
    }

    @Test
    public void testParenthesise() {
        testResult("a & (b | c)", "http://localhost:4000/parenthesise/(a&(b%7Cc))");
    }

    private void testResult(String expected, String url) {
        try {
            URL endpointUrl = new URL(url);
            URLConnection connection = endpointUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            try(Scanner s = new Scanner(in)) {
                s.nextLine();   // Read over initial {\n of JSON

                assertEquals("\t\"result\": \"" + expected + "\",", s.nextLine());
            }
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

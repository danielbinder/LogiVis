package servlet.rest;

import marker.RestEndpoint;
import spark.Spark;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.regex.Matcher;

import static spark.Spark.get;

/**
 * A very simple implementation of a REST API.
 * IMPORTANT: Everything that uses this needs to implement RestEnpoint!
 */
public class REST {
    public static void setup() {
        Spark.port(4000);
    }

    public static void start() {
        try {
            // target = calling class
            RestEndpoint target = (RestEndpoint) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName())
                    .getDeclaredConstructor()
                    .newInstance();

            Arrays.stream(target.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(GET.class))
                    .forEach(m -> get(m.getAnnotation(GET.class).value(),
                                      (req, res) -> {
                                          res.type("application/json");
                                          res.header("Access-Control-Allow-Origin", "*");     // prevents CORS errors
                                          // ALL PARAMETERS IN YOUR METHOD NEED TO BE IN ALPHABETICAL ORDER!
                                          return m.invoke(target, new TreeMap<>(req.params()).values().toArray());
                                      }));
        } catch(ClassNotFoundException |
                IllegalAccessException |
                InvocationTargetException |
                NoSuchMethodException |
                InstantiationException ignored) {}
    }

    public static String preprocess(String raw) {
        return raw.replaceAll(Matcher.quoteReplacement("$"), "\n");
    }
}

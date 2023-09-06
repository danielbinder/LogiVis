package servlet.rest;

import servlet.Servlet;

import java.util.Arrays;
import java.util.TreeMap;

import static spark.Spark.get;

/**
 * A very simple implementation of a REST API.
 */
public class REST {

    public static void start(int port) {
        spark.Spark.port(port);

        Servlet target = new Servlet();        // servlet is hard coded as target

        Arrays.stream(target.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(GET.class))
                .forEach(m -> get(m.getAnnotation(GET.class).value(),
                             (req, res) -> {
                                 res.type("application/json");
                                 res.header("Access-Control-Allow-Origin", "*");     // prevents CORS errors
                                 // ALL PARAMETERS IN YOUR METHOD NEED TO BE IN ALPHABETICAL ORDER!
                                 return m.invoke(target, new TreeMap<>(req.params()).values().toArray());
                             }));
    }
}

package rest;

import servlet.Servlet;

import java.lang.reflect.Method;
import java.util.TreeMap;

import static spark.Spark.get;

/**
 * A very simple implementation of a REST API.
 */
public class REST {

    public static void start(int port) {
        spark.Spark.port(port);

        Servlet servlet = new Servlet();        // servlet is hard coded as target

        for(Method m : servlet.getClass().getDeclaredMethods()) {
            if(m.isAnnotationPresent(GET.class)) {
                get(m.getAnnotation(GET.class).value(),
                    (req, res) -> {
                    res.type("application/json");
                    res.header("Access-Control-Allow-Origin", "*");     // prevents CORS errors
                    return m.invoke(servlet, new TreeMap<>(req.params()).values().toArray());
                });
            }
        }
    }
}

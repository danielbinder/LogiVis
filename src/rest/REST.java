package rest;

import servlet.Servlet;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static spark.Spark.get;

public class REST {
    public static void start(int port) {
        spark.Spark.port(port);

        Servlet servlet = new Servlet();

        for(Method m : servlet.getClass().getDeclaredMethods()) {
            if(m.isAnnotationPresent(GET.class)) {
                get(m.getAnnotation(GET.class).value(),
                    (req, res) -> {
                    res.type("application/json");
                    res.header("Access-Control-Allow-Origin", "*");
                    return m.invoke(servlet, req.params().values().toArray());
                });
            }
        }
    }
}

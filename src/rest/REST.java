package rest;

import servlet.Servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import static spark.Spark.get;

public class REST {
    public static void start(int port) {
        spark.Spark.port(port);

        Servlet servlet = new Servlet();

        for(Method m : servlet.getClass().getDeclaredMethods()) {
            if(m.isAnnotationPresent(GET.class)) {
                get(m.getAnnotation(GET.class).value(),
                    (req, res) -> m.invoke(servlet, req.params().values().toArray()));
            }
        }
    }

    // UTILITY METHODS //

    public static String toJSON(Map<String, String> map) {
        return map.isEmpty() ?
                "{}" :
                map.keySet().stream().reduce("{#", (acc, str) ->
                                acc.replace("#", "\n\"" + str + "\":\"" + map.get(str) + "\",#"))
                        .replace(",#", "\n}");
    }
}

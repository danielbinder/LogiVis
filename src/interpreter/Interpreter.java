package interpreter;

import parser.node.Node;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    /**
     *
     * @param formula Input formula
     * @return Variable assignment
     */
    public static Map<String, String> solve(Node formula) {
        // TODO (doesn't need to be static, but pls change usage in Servlet)
        return Map.of("var1", "true");
    }
}

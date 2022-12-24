package generator;

import parser.node.ActionNode;
import parser.node.Node;

// TODO
public class Generator {
    public static Node generateFormula(String params) {
        return generateFormula(0, 0, 0);
    }

    public static Node generateFormula(int literals, int conjunctions, int clauses) {
        return new ActionNode("a");
    }
}

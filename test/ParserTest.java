import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.logicnode.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Assumption: Lexer is correct!
 */
public class ParserTest {
    @Test
    public void testGeneric() {
        assertEquals(new ImplicationNode(new ActionNode("a"),
                                         new ActionNode("b")),
                     runInput("a -> b"));
    }

    @Test
    public void testPrecedence() {
        assertEquals(new DoubleImplicationNode(new OrNode(new ActionNode("a"),
                                                          new AndNode(new NegationNode(new ActionNode("b")),
                                                                      new ActionNode("c"))),
                                               new AndNode(new NegationNode(new OrNode(new ActionNode("a"),
                                                                                       new ActionNode("b"))),
                                                           new ActionNode("c"))),
                     runInput("a | !b & c <-> !(a | b) & c"));
    }

    private LogicNode runInput(String input) {
        return new Parser().parse(Lexer.tokenize(input));
    }
}

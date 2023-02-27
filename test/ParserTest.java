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

    @Test
    public void testSingleVariable() {
        assertEquals(new ActionNode("a"), runInput("a"));
    }

    @Test
    public void testNestedNegation() {
        assertEquals(new NegationNode(new NegationNode(new ActionNode("a"))), runInput("!!a"));
    }

    @Test
    public void testDistributive() {
        assertEquals(new AndNode(new OrNode(new ActionNode("a"), new ActionNode("b")),
                                 new OrNode(new ActionNode("a"), new ActionNode("c"))),
                     runInput("(a | b) & (a | c)"));
    }

    @Test
    public void testOrderOfOperations() {
        assertEquals(new OrNode(new AndNode(new ActionNode("a"), new ActionNode("b")),
                                new AndNode(new ActionNode("c"), new ActionNode("d"))),
                     runInput("a & b | c & d"));
    }

    private LogicNode runInput(String input) {
        return new Parser().parse(Lexer.tokenize(input));
    }
}

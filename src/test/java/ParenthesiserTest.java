import bool.interpreter.Parenthesiser;
import bool.parser.logicnode.LogicNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParenthesiserTest {
    @Test
    public void testBasic() {
        assertEquals("""
                             ((p1 <-> p0 & !q0 | !p0 & !q0) & (q1 <-> p0 & !q0) | !p1 & (q1 <-> !p0 & !q0)) & ((p2 <-> p1 & !q1 | !p1 & !q1) & (q2 <-> p1 & !q1) | !p2 & (q2 <-> !p1 & !q1)) & !p0 & !q0 & p2 & q2""",
                     Parenthesiser.addNecessaryParenthesis(LogicNode.of("""
                                          ((((p1 <-> ((p0 & !q0) | (!p0 & !q0))) & (q1 <-> ((p0 & !q0)))) |
                                          (!p1 & (q1 <-> ((!p0 & !q0))))) &
                                                                                    
                                          (((p2 <-> ((p1 & !q1) | (!p1 & !q1))) & (q2 <-> ((p1 & !q1)))) |
                                          (!p2 & (q2 <-> ((!p1 & !q1))))))
                                          & ((!p0 & !q0)) & ((p2 & q2))""")));
    }
}

import model.Model;
import model.kripke.KripkeTruthTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KripkeTruthTableTest {
    @Test
    public void testBasic() {
        KripkeTruthTable ktt = Model.of("""
                         # Model = (S, I, T, F) # Type 'this' to use this model or 'compact' for compact
                         S = {s1> [!p !q], s2 [!p q],
                              s3 [p !q], s4< [p q 'deadlock']}            # Set of states
                         I = {s1 ['starting here']}                       # Set of initial states
                         T = {(s1, s2), (s2, s1), (s1, s3), (s3, s1),
                              (s3, s4) ['unsafe transition'], (s4, s1)}   # Set of transitions (s, s')
                         F = {}                         # Set of final states (you can omit empty sets)
                         # For boolean encoding use '>' as suffix for start-, and '<' for goal states""")
                .toKripkeStructure()
                .toKripkeTruthTable();

        assertEquals("""
                             ((((p1 <-> ((p0 & !q0))) & (q1 <-> ((p0 & !q0) | (!p0 & !q0)))) |
                             ((p1 <-> ((!p0 & !q0))) & !q1)) &
                                                          
                             (((p2 <-> ((p1 & !q1))) & (q2 <-> ((p1 & !q1) | (!p1 & !q1)))) |
                             ((p2 <-> ((!p1 & !q1))) & !q2)))
                             & ((!p0 & !q0)) & ((p2 & q2))""", ktt.toFormulaStringWithEncodingStartAndEnd(2));
        assertEquals("""
                             p q || p'q'| p'q'
                             0 1 || 0 0 | 0 0
                             1 0 || 1 1 | 0 0
                             0 0 || 0 1 | 1 0
                             1 1 || 0 0 | 0 0""", ktt.toString());
        assertEquals("""
                             ?p0 ?p1 ?q0 ?q1
                             #p #pnext #q #qnext
                             (
                             (((p <-> p0) & (pnext <-> p1) & (q <-> q0) & (qnext <-> q1)) |
                             ((p <-> p1) & (pnext <-> p2) & (q <-> q1) & (qnext <-> q2)))
                             ->
                             ((((pnext <-> ((p & !q))) & (qnext <-> ((p & !q) | (!p & !q)))) |
                             ((pnext <-> ((!p & !q))) & !qnext)))
                             )
                             & ((!p0 & !q0)) & ((p2 & q2))""", ktt.toQBFString(2));
    }
}

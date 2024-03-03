import model.interpreter.ModelTracer;
import model.parser.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTracerTest {
    @Test
    public void testBasic() {
        ModelTracer mt = new ModelTracer(Model.of("""
                                                      S = {n0 [!a !b !c], n1> [!a !b c], n2 [!a b !c], n3< [!a b c], n4 [a !b !c], n5 [a !b c], n6 [a b !c], n7 [a b c]}
                                                      I = {n1, n6}
                                                      T = {(n0, n5), (n0, n4), (n0, n2), (n0, n3), (n1, n6), (n1, n2), (n2, n5), (n2, n0), (n3, n3), (n4, n6), (n4, n3), (n5, n6), (n5, n0), (n6, n0), (n6, n7), (n7, n6), (n7, n4)}
                                                      """));
        assertEquals("n1 -> n6 -> n0 -> n4 -> n3", mt.trace());
        assertEquals("n1 -> n6 -> n0 -> n3", mt.shortestTrace());
    }
}

import model.parser.Model;
import model.parser.ModelNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class ModelParserTest {
    @Test
    public void basicTest() {
        Model m = new Model();
        ModelNode a = new ModelNode("a", true, false, "x");
        ModelNode b = new ModelNode("b", false, false, "");
        a.successors.put(a, "'to self'");
        a.successors.put(b, "");
        m.addAll(a, b);

        assertEquals(m, Model.of("""
                         S={a [x], b}
                         I = {a}
                         T= {(a, a) ['to self'], (a, b)}"""));

        assertEquals(m, Model.of("""
                a_ [x] -> ['to self'] a,
                a -> b"""));
    }

    @Test
    public void testSingleState() {
        Model m = new Model();
        m.add(new ModelNode("a"));

        assertEquals(m, Model.of("""
                                         S = {a}"""));
        assertEquals(m, Model.of("""
                                         a"""));
    }

    @Test
    public void testComplexSingleState() {
        Model m = new Model();
        m.add(new ModelNode("s0s1"));

        assertEquals(m, Model.of("""
                                              S = {s0s1}"""));
        assertEquals(m, Model.of("""
                                              s0s1"""));
    }

    @Test
    public void testSingleTransition() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.successors.put(a, "");
        m.add(a);

        assertEquals(m, Model.of("""
                                         S = {a}
                                         T = {(a, a)}"""));
        assertEquals(m, Model.of("""
                                         a -> a"""));
    }

    @Test
    public void testSingleLabel() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.label = "'eyo'";
        m.add(a);

        assertEquals(m, Model.of("""
                                         S = {a ['eyo']}"""));
        assertEquals(m, Model.of("""
                                         a ['eyo']"""));
    }

    @Test
    public void testSingleProperty() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.label = "x";
        m.add(a);

        assertEquals(m, Model.of("""
                                         S = {a [x]}"""));
        assertEquals(m, Model.of("""
                                         a [x]"""));
    }

    @Test
    public void testSingleTransitionLabel() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.successors.put(a, "'eyo'");
        m.add(a);

        assertEquals(m, Model.of("""
                                         S = {a}
                                         T = {(a, a) ['eyo']}"""));
        assertEquals(m, Model.of("""
                                         a -> ['eyo'] a"""));
    }

    @Test
    public void testComment() {
        Model m = new Model();
        ModelNode a = new ModelNode("a", true, false, "x");
        ModelNode b = new ModelNode("b", false, false, "");
        a.successors.put(a, "'to self'");
        a.successors.put(b, "");
        m.addAll(a, b);

        assertEquals(m, Model.of("""
                         S={a [x], b} # this is a comment
                         I = {a}
                         # this is also a comment
                         T= {(a, a) ['to self'], (a, b)}
                         # another comment at the end"""));

        assertEquals(m, Model.of("""
                a_ [x] -> ['to self'] a, # this is a comment
                # this is also a comment
                a -> b
                # another comment at the end"""));
    }

    @Test
    public void testError() {
        Model m = new Model();
        ModelNode a = new ModelNode("a", true, false, "x");
        ModelNode b = new ModelNode("b", false, false, "");
        a.successors.put(a, "'to self'");
        a.successors.put(b, "");
        m.addAll(a, b);

        assertThrowsExactly(IllegalArgumentException.class,() -> Model.of("""
                         S={a [x], b} # this is a comment
                         I = {a}
                         # this is also a comment
                         T= {>(a, a) ['to self'], (a, b)}
                         # another comment at the end"""));

        assertThrowsExactly(IllegalArgumentException.class,() -> Model.of("""
                a_ [x] -> ['to self'] a, # this is a comment
                # this is also a comment
                >a -> b
                # another comment at the end"""));
    }

    @Test
    public void testNegativeProperties() {
        Model m = new Model();
        ModelNode a = new ModelNode("a", false, false, "x");
        ModelNode b = new ModelNode("b", false, false, "!x");
        a.successors.put(b, "");
        m.addAll(a, b);

        assertEquals(m, Model.of("""
                                              S = {a [x], b [!x]}
                                              T = {(a, b)}"""));

        assertEquals(m, Model.of("a [x] -> b [!x]"));
    }

    @Test
    public void testDeadlockAutomaton() {
        Model m = new Model();
        ModelNode s1 = new ModelNode("s1", true, false, "!p !q");
        ModelNode s2 = new ModelNode("s2", false, false, "!p q");
        ModelNode s3 = new ModelNode("s3", false, false, "p !q");
        ModelNode s4 = new ModelNode("s4", false, false, "p q 'deadlock'");
        s1.isEncodingStartPoint = true;
        s4.isEncodingEndPoint = true;
        s1.successors.put(s2, "");
        s2.successors.put(s1, "");
        s1.successors.put(s3, "");
        s3.successors.put(s1, "");
        s3.successors.put(s4, "'unsafe transition'");
        s4.successors.put(s1, "");
        m.addAll(s1, s2, s3, s4);


        assertEquals(m, Model.of("""
                  # Model = (S, I, T, F) # Type 'this' to use this model or 'compact' for compact
                  S = {s1> [!p !q], s2 [!p q],
                       s3 [p !q], s4< [p q 'deadlock']}            # Set of states
                  I = {s1 ['starting here']}                       # Set of initial states
                  T = {(s1, s2), (s2, s1), (s1, s3), (s3, s1),
                       (s3, s4) ['unsafe transition'], (s4, s1)}   # Set of transitions (s, s')
                  F = {}                         # Set of final states (you can omit empty sets)
                  # For boolean encoding use '>' as suffix for start-, and '<' for goal states"""));

        assertEquals(m, Model.of("""
                  # Type 'compact' to use this model
                  # Initial states are denoted by '_' as suffix, final states by '*'
                  # For boolean encoding use '>' as suffix for start-, and '<' for goal states
                  # Both states and transitions can be labeled with '['Text: ' var1 var2]'
                  # Transitions are denoted by either '->' for unidirectional transitions
                  # or '-' for bidirectional transitions
                  s1_> [!p !q] - s2 [!p q], s1 - s3 [p !q],s3 -> ['unsafe transition'] s4< [p q 'deadlock'], s4 -> s1"""));
    }

    @Test
    public void testEOF() {
        Model m = new Model();
        ModelNode s0 = new ModelNode("s0", true, false, "");
        ModelNode s1 = new ModelNode("s1", false, false, "");
        ModelNode sink = new ModelNode("sink", false, false, "");
        ModelNode s2s3 = new ModelNode("s2s3", false, false, "");
        ModelNode s2s4 = new ModelNode("s2s4", false, true, "");
        ModelNode s2 = new ModelNode("s2", false, false, "");
        s0.successors.put(s1, "a");
        s0.successors.put(sink, "b");
        s1.successors.put(sink, "a");
        s1.successors.put(s2s3, "b");
        sink.successors.put(sink, "a b");
        s2s3.successors.put(s2s3, "b");
        s2s3.successors.put(s2s4, "a");
        s2s4.successors.put(s2s3, "b");
        s2s4.successors.put(s2, "a");
        s2.successors.put(s2, "a");
        s2.successors.put(s2s3, "b");
        m.addAll(s0, s1, s2, sink, s2s3, s2s4);

        assertEquals(m, Model.of("""
                                              s0_ -> [a] s1, s0 -> [b] sink, s1 -> [a] sink, sink -> [a b] sink,
                                              s1 -> [b] s2s3, s2s3 -> [b] s2s3, s2s3 -> [a] s2s4*, s2s4 -> [b] s2s3,
                                              s2s4 -> [a] s2, s2 -> [a] s2, s2 -> [b] s2s3"""));
    }
}

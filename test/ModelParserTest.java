import model.ModelNode;
import org.junit.jupiter.api.Test;
import model.Model;

import static org.junit.jupiter.api.Assertions.*;

public class ModelParserTest {
    @Test
    public void basicTest() {
        Model m = new Model();
        ModelNode a = new ModelNode("a", true, false, "x");
        ModelNode b = new ModelNode("b", false, false, "");
        a.successors.put(a, "'to self'");
        a.successors.put(b, "");
        m.addAll(a, b);

        assertEqualModels(m, Model.of("""
                         S={a [x], b}
                         I = {a}
                         T= {(a, a) ['to self'], (a, b)}"""));

        assertEqualModels(m, Model.of("""
                a_ [x] -> ['to self'] a,
                a -> b"""));
    }

    @Test
    public void testSingleState() {
        Model m = new Model();
        m.add(new ModelNode("a"));

        assertEqualModels(m, Model.of("""
                                         S = {a}"""));
        assertEqualModels(m, Model.of("""
                                         a"""));
    }

    @Test
    public void testSingleTransition() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.successors.put(a, "");
        m.add(a);

        assertEqualModels(m, Model.of("""
                                         S = {a}
                                         T = {(a, a)}"""));
        assertEqualModels(m, Model.of("""
                                         a -> a"""));
    }

    @Test
    public void testSingleLabel() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.label = "'eyo'";
        m.add(a);

        assertEqualModels(m, Model.of("""
                                         S = {a ['eyo']}"""));
        assertEqualModels(m, Model.of("""
                                         a ['eyo']"""));
    }

    @Test
    public void testSingleProperty() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.label = "x";
        m.add(a);

        assertEqualModels(m, Model.of("""
                                         S = {a [x]}"""));
        assertEqualModels(m, Model.of("""
                                         a [x]"""));
    }

    @Test
    public void testSingleTransitionLabel() {
        Model m = new Model();
        ModelNode a = new ModelNode("a");
        a.successors.put(a, "'eyo'");
        m.add(a);

        assertEqualModels(m, Model.of("""
                                         S = {a}
                                         T = {(a, a) ['eyo']}"""));
        assertEqualModels(m, Model.of("""
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

        assertEqualModels(m, Model.of("""
                         S={a [x], b} # this is a comment
                         I = {a}
                         # this is also a comment
                         T= {(a, a) ['to self'], (a, b)}
                         # another comment at the end"""));

        assertEqualModels(m, Model.of("""
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

        assertEqualModels(m, Model.of("""
                                              S = {a [x], b [!x]}
                                              T = {(a, b)}"""));

        assertEqualModels(m, Model.of("a [x] -> b [!x]"));
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


        assertEqualModels(m, Model.of("""
                  # Model = (S, I, T, F) # Type 'this' to use this model or 'compact' for compact
                  S = {s1> [!p !q], s2 [!p q],
                       s3 [p !q], s4< [p q 'deadlock']}            # Set of states
                  I = {s1 ['starting here']}                       # Set of initial states
                  T = {(s1, s2), (s2, s1), (s1, s3), (s3, s1),
                       (s3, s4) ['unsafe transition'], (s4, s1)}   # Set of transitions (s, s')
                  F = {}                         # Set of final states (you can omit empty sets)
                  # For boolean encoding use '>' as suffix for start-, and '<' for goal states"""));

        assertEqualModels(m, Model.of("""
                  # Type 'compact' to use this model
                  # Initial states are denoted by '_' as suffix, final states by '*'
                  # For boolean encoding use '>' as suffix for start-, and '<' for goal states
                  # Both states and transitions can be labeled with '['Text: ' var1 var2]'
                  # Transitions are denoted by either '->' for unidirectional transitions
                  # or '-' for bidirectional transitions
                  s1_> [!p !q] - s2 [!p q], s1 - s3 [p !q],s3 -> ['unsafe transition'] s4< [p q 'deadlock'], s4 -> s1"""));
    }

    // H E L P E R S
    private void assertEqualModels(Model expected, Model actual) {
        assertEquals(expected.toString(), actual.toString());
    }
}

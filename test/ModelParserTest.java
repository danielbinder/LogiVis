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

    // H E L P E R S
    private void assertEqualModels(Model expected, Model actual) {
        assertEquals(expected.toString(), actual.toString());
    }
}

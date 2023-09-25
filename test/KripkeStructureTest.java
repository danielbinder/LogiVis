import model.kripke.KripkeGenerator;
import model.kripke.KripkeStructure;
import model.parser.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KripkeStructureTest {
    @Test
    public void testToFormula() {
        KripkeStructure ks = KripkeGenerator.generate("4_2_3_1_3_true");
        System.out.println(ks);
        System.out.println(ks.toFormulaString(3));
        System.out.println(ks.toFormula(3));
    }

    @Test
    public void testToAndFromString() {
        KripkeStructure ks = KripkeGenerator.generate("4_2_3_1_3_true");
        assertEquals(Model.of(ks), Model.of(KripkeStructure.fromString(ks.toString())));
    }
}

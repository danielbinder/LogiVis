import model.kripke.KripkeGenerator;
import model.kripke.KripkeStructure;
import model.parser.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KripkeStructureTest {
    @Test
    public void testToFormula() {
        KripkeStructure ks = KripkeGenerator.generate(4,2,3,1,3,true);
        System.out.println(ks);
        System.out.println(ks.toFormulaString(3));
        System.out.println(ks.toFormula(3));
    }

    @Test
    public void testToAndFromString() {
        KripkeStructure ks = KripkeGenerator.generate(4,2,3,1,3,true);
        assertEquals(Model.of(ks), Model.of(KripkeStructure.fromString(ks.toString())));
    }
}

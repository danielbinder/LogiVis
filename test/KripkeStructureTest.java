import generator.Generator;
import generator.kripke.KripkeStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KripkeStructureTest {
    @Test
    public void testToFormula() {
        KripkeStructure ks = Generator.generateKripkeStructure("4_2_3_1_3_true");
        System.out.println(ks);
        System.out.println(ks.toFormulaString(3));
        System.out.println(ks.toFormula(3));
    }

    @Test
    public void testToAndFromString() {
        KripkeStructure ks = Generator.generateKripkeStructure("4_2_3_1_3_true");
        String kripkeString = ks.toString();
        assertEquals(kripkeString, KripkeStructure.fromString(kripkeString).toString());
    }
}

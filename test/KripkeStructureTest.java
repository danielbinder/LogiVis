import generator.Generator;
import generator.kripke.KripkeStructure;
import org.junit.jupiter.api.Test;

public class KripkeStructureTest {
    @Test
    public void testToFormula() {
        KripkeStructure ks = Generator.generateKripkeStructure("4_2_3_1_3_true");
        System.out.println(ks);
        System.out.println(ks.toFormula(3));
    }
}

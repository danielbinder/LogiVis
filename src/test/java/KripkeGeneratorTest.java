import model.variant.kripke.KripkeGenerator;
import model.variant.kripke.KripkeStructure;
import org.junit.jupiter.api.Test;

public class KripkeGeneratorTest {
    @Test
    public void testParameters() {
        int nodes = 4;
        int initialNodes = 2;
        int variables = 3;
        int minSuccessors = 1;
        int maxSuccessors = 3;
        boolean allStatesReachable = true;

        for(int i = 0; i < 10; i++) {
            KripkeStructure ks = KripkeGenerator.generate(nodes, initialNodes, variables, minSuccessors, maxSuccessors, allStatesReachable);

            System.out.println(ks);
        }
        // E.g. 0;a:false+b:false+c:false;false;2_1;a:false+b:false+c:true;false;0_2;a:false+b:true+c:false;true;3_3;a:true+b:false+c:true;true;3+0
    }
}

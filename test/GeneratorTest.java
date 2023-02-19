import generator.Generator;
import generator.kripke.KripkeStructure;
import org.junit.jupiter.api.Test;

public class GeneratorTest {
    @Test
    public void testParameters() {
        int nodes = 4;
        int initialNodes = 2;
        int variables = 3;
        int minSuccessors = 1;
        int maxSuccessors = 3;
        boolean allStatesReachable = true;

        KripkeStructure ks = Generator.generateKripkeStructure(nodes + "_" +
                                                                       initialNodes + "_" +
                                                                       variables + "_" +
                                                                       minSuccessors + "_" +
                                                                       maxSuccessors + "_" +
                                                                       allStatesReachable);

        System.out.println(ks);
        // E.g. 0;a:false+b:false+c:false;true;2+3_1;a:false+b:false+c:false;false;0+1_2;a:false+b:false+c:false;false;3_3;a:false+b:false+c:false;true;2+3
    }
}

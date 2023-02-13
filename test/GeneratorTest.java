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

        KripkeStructure ks = Generator.generateKripkeStructure(nodes + ";" +
                                                                       initialNodes + ";" +
                                                                       variables + ";" +
                                                                       minSuccessors + ";" +
                                                                       maxSuccessors + ";" +
                                                                       allStatesReachable);

        System.out.println(ks);
    }
}

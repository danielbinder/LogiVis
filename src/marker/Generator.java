package marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static model.kripke.KripkeGenerator.rand;

public interface Generator {
    Random rand = new Random();

    static void error(String message) {
        throw new IllegalArgumentException(message);
    }

    static List<Integer> pickRandom(int bound, int amount) {
        if(amount > bound) error("Can't pick " + amount + " out of " + bound);
        List<Integer> picks = new ArrayList<>();

        while(amount > 0) {
            int pick = rand.nextInt(bound) & Integer.MAX_VALUE;

            if(!picks.contains(pick)) {
                picks.add(pick);
                amount--;
            }
        }

        return picks;
    }
}

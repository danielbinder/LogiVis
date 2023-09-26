package marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static model.kripke.KripkeGenerator.rand;

public interface Generator {
    Random rand = new Random();

    static void error(String message) {
        throw new IllegalArgumentException(message);
    }

    static int pickRandom(int bound) {
        return rand.nextInt(bound) & Integer.MAX_VALUE;
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

    static List<Integer> pickRandomRepeatable(int bound, int amount) {
        List<Integer> picks = new ArrayList<>();

        while(amount-- > 0) picks.add(rand.nextInt(bound) & Integer.MAX_VALUE);

        return picks;
    }

    static List<String> generateVariableNames(int amount) {
        return IntStream.range(0, amount)
                .mapToObj(i -> ((char) ('a' + (i % 26))) + String.valueOf(i >= 26 ? i / 26 : ""))
                .toList();
    }
}

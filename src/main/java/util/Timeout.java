package util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Timeout {
    public static <T> T of(Supplier<T> supplier, long timeoutDuration, TimeUnit timeUnit) {
        Logger.info("Starting process with timeout set to " + timeoutDuration + " " + timeUnit.name());

        AtomicReference<T> result = new AtomicReference<>();
        Thread t = Thread.ofPlatform().start(() -> result.set(supplier.get()));
        long startTime = System.nanoTime();

        try {
            t.join(timeUnit.toMillis(timeoutDuration), (int) (timeUnit.toNanos(timeoutDuration) % 1_000_000));
        } catch(InterruptedException e) {
            throw new RuntimeException("Timed out after " + (System.nanoTime() - startTime) / 1_000_000_000.0 + "s");
        }

        Logger.info("Calculation took " + (System.nanoTime() - startTime) / 1_000_000_000.0 + "s");

        return result.get();
    }
}

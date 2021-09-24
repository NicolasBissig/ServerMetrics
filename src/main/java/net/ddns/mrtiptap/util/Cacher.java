package net.ddns.mrtiptap.util;

import lombok.RequiredArgsConstructor;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Cacher<T> {
    private final Supplier<T> supply;
    private final int maxAccesses;

    private T cachedResult = null;
    private int accesses = -1;

    public T get() {
        if (accesses == -1 || accesses >= maxAccesses) {
            cachedResult = supply.get();
            accesses = 0;
        }

        accesses++;
        return cachedResult;
    }
}

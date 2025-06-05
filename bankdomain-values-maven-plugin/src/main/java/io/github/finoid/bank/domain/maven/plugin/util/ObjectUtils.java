package io.github.finoid.bank.domain.maven.plugin.util;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class ObjectUtils {
    private ObjectUtils() {
    }

    public static <T> T valueOrFallback(@Nullable final T origin, final Supplier<T> fallback) {
        if (origin != null) {
            return origin;
        }

        return fallback.get();
    }
}

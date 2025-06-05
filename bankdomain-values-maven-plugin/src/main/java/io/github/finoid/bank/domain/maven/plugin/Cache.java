package io.github.finoid.bank.domain.maven.plugin;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Cache<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();

    /**
     * Checks whether a key exists as a cached entry.
     *
     * @param key the cache key
     * @return true if exists, false otherwise.
     */
    public boolean contains(final K key) {
        return cache.containsKey(key);
    }

    /**
     * Puts a new value into the cache. The previous value is overwritten.
     *
     * @param key   the cache key
     * @param value the to be cached value
     * @return the previous cached value
     */
    public V put(final K key, final V value) {
        return cache.put(key, value);
    }

    /**
     * Returns a cached value by key, or inserts a new via the provided supplier.
     *
     * @param key             the cache key
     * @param defaultSupplier the default value supplier
     * @return the cache value
     */
    @Nullable
    @SuppressWarnings("return")
    public V getOrDefault(final K key, final Supplier<V> defaultSupplier) {
        if (contains(key)) {
            return cache.get(key);
        }

        final V value = defaultSupplier.get();

        cache.put(key, value);

        return value;
    }

}
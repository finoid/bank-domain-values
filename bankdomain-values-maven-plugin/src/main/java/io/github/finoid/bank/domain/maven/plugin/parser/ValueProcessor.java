package io.github.finoid.bank.domain.maven.plugin.parser;

/**
 * Functional interface representing a consumer of parsed values.
 * <p>
 * This is typically used in streaming or line-by-line parsing scenarios where
 * each parsed object of type {@code T} is immediately processed, stored, or forwarded.
 *
 * @param <T> the type of value to be processed
 */
@FunctionalInterface
public interface ValueProcessor<T> {
    /**
     * Processes the given parsed value.
     *
     * @param value the value to process; never {@code null}
     */
    void process(final T value);
}

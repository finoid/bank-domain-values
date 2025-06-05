package io.github.finoid.bank.domain.maven.plugin.parser;

import io.github.finoid.bank.domain.maven.plugin.parser.csv.LineParserContext;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Generic parser interface for reading structured input (e.g., CSV) and transforming it into domain-specific objects.
 *
 * @param <T> the type of object to be parsed from the input
 */
public interface Parser<T> {
    /**
     * Parses the provided {@link InputStream} line by line using the given {@link LineParserContext},
     * and processes each parsed value using the supplied {@link ValueProcessor}.
     *
     * @param inputStream    the input source to be parsed (e.g., a CSV file)
     * @param context        the context containing delimiter settings and line parsing logic
     * @param valueProcessor a consumer that handles each parsed value
     */
    void parse(final InputStream inputStream, final LineParserContext<T> context, final ValueProcessor<T> valueProcessor);

    /**
     * Parses the provided {@link InputStream} and returns a {@link Stream} of parsed objects.
     * <p>
     * Note: The caller is responsible for closing the stream and ensuring its lifecycle is managed properly.
     *
     * @param inputStream the input source to be parsed (e.g., a CSV file)
     * @param context     the context containing delimiter settings and line parsing logic
     * @return a stream of parsed values
     */
    Stream<T> parse(final InputStream inputStream, final LineParserContext<T> context);
}


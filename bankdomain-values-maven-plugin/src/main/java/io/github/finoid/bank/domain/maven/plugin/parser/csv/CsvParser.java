package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import io.github.finoid.bank.domain.maven.plugin.parser.Parser;
import io.github.finoid.bank.domain.maven.plugin.parser.ValueProcessor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CsvParser<T> implements Parser<T> {
    private final LineParser lineParser; // TODO (nw) as in-parameter or composition

    @Override
    @SneakyThrows
    public void parse(final InputStream inputStream, final LineParserContext<T> context,
                      final ValueProcessor<T> valueProcessor) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (context.isSkipFirstLine()) {
                reader.readLine(); // skip header
            }

            reader.lines()
                .filter(line -> filterInvalidLine(context, line))
                .forEach(line -> {
                    final T lineType = lineParser.parse(context, line);

                    valueProcessor.process(lineType);
                });
        }
    }

    @Override
    @SneakyThrows
    public Stream<T> parse(final InputStream inputStream, final LineParserContext<T> context) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        if (context.isSkipFirstLine()) {
            reader.readLine(); // skip header
        }

        return reader.lines()
            .filter(line -> filterInvalidLine(context, line))
            .map(line -> lineParser.parse(context, line));

    }

    @SuppressWarnings("argument")
    private static boolean filterInvalidLine(final LineParserContext<?> context, final String line) {
        if (!context.getLineValidator().isValid(context, line.split(context.getDelimiter()))) {
            // TODO (nw) add log message?
            return false;
        }

        return true;
    }
}

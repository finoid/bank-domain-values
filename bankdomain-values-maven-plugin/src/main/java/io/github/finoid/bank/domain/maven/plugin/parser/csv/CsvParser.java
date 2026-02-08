package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import io.github.finoid.bank.domain.maven.plugin.parser.Parser;
import io.github.finoid.bank.domain.maven.plugin.parser.ValueProcessor;
import io.github.finoid.bank.domain.maven.plugin.util.Precondition;
import lombok.SneakyThrows;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@Singleton
@Named
public class CsvParser<T> implements Parser<T> {
    private final LineParser lineParser;

    @Inject
    public CsvParser(final LineParser lineParser) {
        this.lineParser = Precondition.nonNull(lineParser, "LineParser must not be null.");
    }

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

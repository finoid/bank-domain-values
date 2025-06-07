package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import com.google.common.base.Splitter;
import io.github.finoid.bank.domain.maven.plugin.Range;
import io.github.finoid.bank.domain.maven.plugin.exceptions.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.component.annotations.Component;
import org.jspecify.annotations.Nullable;

import javax.inject.Singleton;
import java.util.List;

/**
 * Converts a delimited string into an {@link Range} instance.
 * Expected format: {@code startIndex<delimiter>endIndex" (e.g., "1:2")}
 */
@Singleton
@Component(role = Converter.class, hint = "range")
public class RangeConverter implements Converter<String, Range<Integer>> {
    @Override
    public Range<Integer> convert(@Nullable String value, final ConverterContext converterContext) {
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Account types value is missing or empty");
        }

        try {
            final String delimiter = converterContext.getNoneEmptyOrThrow("delimiter");

            final List<String> parts = Splitter.onPattern(delimiter).splitToList(value);
            if (parts.size() != 2) {
                throw new ParseException("Expected two parts for account types, but got: '" + value + "'");
            }

            final int start = Integer.parseInt(parts.get(0).trim());
            final int end = Integer.parseInt(parts.get(1).trim());

            return Range.of(start, end);
        } catch (final NumberFormatException e) {
            throw new ParseException("Invalid number format in account types: '" + value + "'", e);
        }
    }
}

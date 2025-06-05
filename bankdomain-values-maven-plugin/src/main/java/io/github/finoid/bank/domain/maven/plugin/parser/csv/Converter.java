package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import org.jspecify.annotations.Nullable;
import io.github.finoid.bank.domain.maven.plugin.exceptions.ParseException;

public interface Converter<T, R> {
    /**
     * Converts {@code value} into a {@link R} instance.
     *
     * @param value            the value to be converted
     * @param converterContext the {@link ConverterContext}
     * @return the converted value
     * @throws ParseException in case an error occurs
     */
    @Nullable
    R convert(final @Nullable T value, final ConverterContext converterContext);
}

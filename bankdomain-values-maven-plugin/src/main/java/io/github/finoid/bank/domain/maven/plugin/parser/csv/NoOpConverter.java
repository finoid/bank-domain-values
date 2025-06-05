package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import org.jspecify.annotations.Nullable;

public final class NoOpConverter implements Converter<String, Object> {
    @Override
    @Nullable
    public String convert(final @Nullable String value, final ConverterContext converterContext) {
        return value;
    }
}

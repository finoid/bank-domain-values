package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import org.jspecify.annotations.Nullable;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("noOp")
public final class NoOpConverter implements Converter<String, Object> {
    @Override
    @Nullable
    public String convert(final @Nullable String value, final ConverterContext converterContext) {
        return value;
    }
}

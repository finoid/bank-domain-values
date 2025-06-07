package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import org.codehaus.plexus.component.annotations.Component;
import org.jspecify.annotations.Nullable;

import javax.inject.Singleton;

@Singleton
@Component(role = Converter.class, hint = "noOp")
public final class NoOpConverter implements Converter<String, Object> {
    @Override
    @Nullable
    public String convert(final @Nullable String value, final ConverterContext converterContext) {
        return value;
    }
}

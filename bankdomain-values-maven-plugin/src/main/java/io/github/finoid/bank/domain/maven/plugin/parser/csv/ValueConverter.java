package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor
public class ValueConverter {
    private final Map<Class<?>, Converter<String, ?>> converters;

    @Nullable
    public Object convertValue(final Class<? extends Converter<?, ?>> converterClazz, final ConverterContext converterContext, final String value) {
        try {
            if (!converters.containsKey(converterClazz)) {
                throw new RuntimeException("No converter for class " + converterClazz.getName());
            }

            return converters.get(converterClazz)
                .convert(value, converterContext);
        } catch (final Exception e) {
            throw new RuntimeException(e); // TODO (nw) contextual converter exception?
        }
    }
}

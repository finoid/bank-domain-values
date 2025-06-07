package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import io.github.finoid.bank.domain.maven.plugin.util.Precondition;
import org.codehaus.plexus.component.annotations.Component;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Component(role = ValueConverter.class)
public class ValueConverter {
    private final Map<Class<?>, Converter<String, ?>> converters;

    @Inject
    public ValueConverter(final List<Converter<String, ?>> converters) {
        this.converters = Precondition.nonNull(converters, "Converts must not be null.").stream()
            .collect(Collectors.toMap(Converter::getClass, it -> it));
    }

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

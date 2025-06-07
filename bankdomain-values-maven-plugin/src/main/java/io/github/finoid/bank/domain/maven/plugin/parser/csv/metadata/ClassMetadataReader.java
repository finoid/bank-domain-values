package io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata;

import io.github.finoid.bank.domain.maven.plugin.Cache;
import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.component.annotations.Component;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Component(role = ClassMetadataReader.class)
@RequiredArgsConstructor
public class ClassMetadataReader {
    private final Cache<Class<?>, ClassMetadata> cache = new Cache<>();

    private static <T> FieldMetadataCollection fieldsOfClass(final Class<T> clazz) {
        final List<FieldMetadata> fields = new ArrayList<>();

        for (final Field field : clazz.getDeclaredFields()) {
            fields.add(FieldMetadata.of(field.getName(), field.getType(), field));
        }

        return FieldMetadataCollection.ofFields(fields);
    }

    /**
     * Reads the metadata of the provided {@code clazz}.
     *
     * @param clazz the class to be read.
     * @param <T>   the class type
     * @return the class metadata of the provided clazz
     */
    @SuppressWarnings("NullAway")
    public <T> ClassMetadata readClass(final Class<T> clazz) {
        return cache.getOrDefault(clazz, () -> classMetadataOfClass(clazz));
    }

    private <T> ClassMetadata classMetadataOfClass(final Class<T> clazz) {
        return ClassMetadata.ofFields(fieldsOfClass(clazz));
    }
}

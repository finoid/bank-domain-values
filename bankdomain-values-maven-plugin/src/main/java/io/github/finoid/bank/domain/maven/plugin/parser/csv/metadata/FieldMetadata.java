package io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldMetadata {
    String name;
    Class<?> type;
    Field field;

    public static FieldMetadata of(final String name, final Class<?> type, final Field field) {
        return new FieldMetadata(name, type, field);
    }

    /**
     * Returns true whether the provided {@code annotationClazz} exists as an annotation, false otherwise.
     *
     * @param annotationClazz the annotation class.
     * @return true if exists, false otherwise.
     */
    public boolean hasAnnotation(final Class<? extends Annotation> annotationClazz) {
        return field.getAnnotationsByType(annotationClazz).length > 0;
    }

    /**
     * Returns a list of {@link Annotation} by {@code annotationClazz}.
     *
     * @param annotationClazz the annotation class
     * @param <T>             annotation class type
     * @return list of annotations
     */
    public <T extends Annotation> List<T> getAnnotationBy(final Class<T> annotationClazz) {
        return Arrays.asList(field.getAnnotationsByType(annotationClazz));
    }
}

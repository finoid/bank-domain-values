package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import io.github.finoid.bank.domain.maven.plugin.exceptions.ParseException;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata.ClassMetadata;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata.ClassMetadataReader;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata.FieldMetadata;
import io.github.finoid.bank.domain.maven.plugin.util.Precondition;
import io.github.finoid.bank.domain.maven.plugin.util.ReflectionUtils;
import io.github.finoid.bank.domain.maven.plugin.util.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.component.annotations.Component;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Responsible for parsing a single line of structured text (e.g., CSV)
 * into an instance of a domain-specific object {@code T}.
 * <p>
 * Parsing is performed using reflection and annotation metadata via {@link Column},
 * with optional converters specified for type-safe transformation of field values.
 */
@Singleton
@Component(role = LineParser.class)
public class LineParser {
    private final ValueConverter valueConverter;
    private final ClassMetadataReader classMetadataReader;

    @Inject
    public LineParser(final ValueConverter valueConverter, final ClassMetadataReader classMetadataReader) {
        this.valueConverter = Precondition.nonNull(valueConverter, "ValueConverter must not be null.");
        this.classMetadataReader = Precondition.nonNull(classMetadataReader, "ClassMetadataReader must not be null.");
    }

    /**
     * Parses a line of input text into an object of type {@code T}, using the provided context.
     *
     * @param context the line parser context, including delimiter and target type
     * @param line    the line of input text to parse
     * @param <T>     the target type to be instantiated and populated
     * @return a populated instance of type {@code T}
     * @throws ParseException if parsing or field population fails
     */
    @SuppressWarnings({"argument", "return"})
    public <T> T parse(final LineParserContext<T> context, final String line) {
        try {
            final String[] tokens = line.split(context.getDelimiter(), -1);

            final T newInstance = ReflectionUtils.newInstance(context.getLineType());

            final ClassMetadata classMetadata = classMetadataReader.readClass(context.getLineType());

            handleFields(newInstance, classMetadata, tokens);

            return newInstance;
        } catch (final Exception e) {
            throw new ParseException("Unexpected parse exception. Cause: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings({"method.invocation", "array.access.unsafe.low", "array.access.unsafe.high.constant", "array.access.unsafe.high"})
    private <T> void handleFields(final T newInstance, final ClassMetadata classMetadata, final String[] tokens) {
        final List<FieldMetadata> fields = classMetadata.getFieldsAnnotatedWith(Column.class);

        fields.forEach(field -> {
            final Column column = getColumnAnnotationOfField(field)
                .orElseThrow();

            final String token = tokens[column.indice()]; // might generate ArrayIndexOutOfBoundsException

            final Class<? extends Converter<String, ?>> converterClazz = column.converter()
                .type();

            final Map<String, String> converterMetadata = Arrays.stream(column.converter().context()
                    .split("\\$"))
                .filter(StringUtils::isNoneBlank)
                .map(entry -> entry.split("=", 2))
                .filter(parts -> parts.length == 2 && StringUtils.isNoneBlank(parts[0], parts[1]))
                .collect(Collectors.toMap(parts -> parts[0].trim(), it -> it[1].trim()));

            final ConverterContext converterContext = ConverterContext.ofMetadata(converterMetadata);

            handleFieldValue(newInstance, field.getField(), converterClazz, converterContext, token);
        });
    }

    private <T> void handleFieldValue(final T newInstance, final Field field, final Class<? extends Converter<?, ?>> converterClazz,
                                      final ConverterContext converterContext, @Nullable final String value) {
        if (value == null) {
            return;
        }

        ReflectionUtils.setValue(newInstance, field,
            valueConverter.convertValue(converterClazz, converterContext, value));
    }

    private static Optional<Column> getColumnAnnotationOfField(final FieldMetadata fieldMetadata) {
        final List<Column> columnsPoc = fieldMetadata.getAnnotationBy(Column.class);

        return columnsPoc.stream()
            .reduce(StreamUtils.toOne());
    }
}

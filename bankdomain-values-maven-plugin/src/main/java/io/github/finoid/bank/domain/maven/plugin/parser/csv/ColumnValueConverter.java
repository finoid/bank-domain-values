package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used within {@link Column} to define a custom converter for transforming
 * a raw CSV string value into a target object type.
 *
 * <p>This allows fine-grained control over how individual CSV columns are parsed and mapped.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @Column(
 *     indice = 2,
 *     converter = @ColumnValueConverter(type = CustomDateConverter.class, context = "yyyy-MM-dd")
 * )
 * private LocalDate startDate;
 * }</pre>
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ColumnValueConverter {
    Class<? extends Converter<String, ?>> type() default NoOpConverter.class;

    String context() default "";
}

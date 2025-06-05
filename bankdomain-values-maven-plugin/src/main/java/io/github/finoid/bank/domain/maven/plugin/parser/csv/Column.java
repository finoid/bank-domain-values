package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark a field or method for CSV column mapping during parsing.
 * <p>
 * This is typically used to bind a specific column (by index) to a class property, along with an optional converter.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @Column(indice = 0, converter = @ColumnValueConverter(SomeConverter.class))
 * private String accountNumber;
 * }
 * </pre>
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {
    int indice();

    ColumnValueConverter converter() default @ColumnValueConverter;

    String converterContext() default "";
}

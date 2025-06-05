package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import io.github.finoid.bank.domain.maven.plugin.util.ObjectUtils;
import io.github.finoid.bank.domain.maven.plugin.util.Precondition;
import lombok.Value;

/**
 * Represents the context configuration for a {@link LineParser} operation.
 * Encapsulates parsing settings like delimiter, type, column expectations, and optional line validation.
 *
 * @param <T> the target type to be parsed from the input line
 */
@Value
public class LineParserContext<T> {
    String delimiter;
    int expectedColumnCount;
    boolean skipFirstLine;
    Class<T> lineType;
    LineValidator lineValidator;

    LineParserContext(final String delimiter, final int expectedColumnCount, final boolean skipFirstLine, final Class<T> lineType,
                      final LineValidator lineValidator) {
        this.delimiter = delimiter;
        this.expectedColumnCount = expectedColumnCount;
        this.skipFirstLine = skipFirstLine;
        this.lineType = lineType;
        this.lineValidator = lineValidator;
    }

    public static <T> LineParserContextBuilder<T> builder() {
        return new LineParserContextBuilder<T>();
    }

    @SuppressWarnings("NullAway.Init")
    public static class LineParserContextBuilder<T> {
        private String delimiter;
        private int expectedColumnCount;
        private boolean skipFirstLine;
        private Class<T> lineType;
        private LineValidator lineValidator = new LineValidator.AlwaysTrue();

        LineParserContextBuilder() {
        }

        public LineParserContextBuilder<T> delimiter(final String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public LineParserContextBuilder<T> expectedColumnCount(final int expectedColumnCount) {
            this.expectedColumnCount = expectedColumnCount;
            return this;
        }

        public LineParserContextBuilder<T> skipFirstLine(final boolean skipFirstLine) {
            this.skipFirstLine = skipFirstLine;
            return this;
        }

        public LineParserContextBuilder<T> lineType(final Class<T> lineType) {
            this.lineType = lineType;
            return this;
        }

        public LineParserContextBuilder<T> lineValidator(final LineValidator lineValidator) {
            this.lineValidator = lineValidator;
            return this;
        }

        public LineParserContext<T> build() {
            return new LineParserContext<>(
                Precondition.nonNull(this.delimiter, "Delimiter must be set"),
                Precondition.gt(this.expectedColumnCount, 0, "Expected column count must be greater than 0"),
                this.skipFirstLine,
                Precondition.nonNull(this.lineType, "LineType must be set"),
                ObjectUtils.valueOrFallback(this.lineValidator, LineValidator.AlwaysTrue::new)
            );
        }
    }
}

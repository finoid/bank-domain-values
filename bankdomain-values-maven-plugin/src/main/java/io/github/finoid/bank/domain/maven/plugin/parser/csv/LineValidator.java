package io.github.finoid.bank.domain.maven.plugin.parser.csv;

/**
 * Strategy interface for validating a parsed line (represented as tokenized columns) before it is processed.
 * <p>
 * Implementations can be used to filter out malformed, incomplete, or irrelevant rows from being parsed into objects.
 */
public interface LineValidator {
    /**
     * Validates the parsed tokens of a line based on the given parser context.
     *
     * @param context the parsing context containing delimiter, expected column count, etc.
     * @param tokens  the line split into individual column values
     * @return {@code true} if the line is valid and should be parsed; {@code false} to skip it
     */
    boolean isValid(final LineParserContext<?> context, final String[] tokens); // TODO (nw) Result value class instead, propagate error cause?

    /**
     * Default implementation of {@link LineValidator} that always returns {@code true}.
     * <p>
     * Can be used as a no-op validator when all lines should be accepted.
     */
    class AlwaysTrue implements LineValidator {
        @Override
        public boolean isValid(final LineParserContext<?> context, final String[] tokens) {
            return true;
        }
    }
}

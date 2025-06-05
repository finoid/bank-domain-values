package io.github.finoid.bank.domain.maven.plugin.parser.csv;

public class BankAccountLineValidator implements LineValidator {
    @Override
    @SuppressWarnings("array.access.unsafe.high.constant")
    public boolean isValid(final LineParserContext<?> context, final String[] tokens) {
        if (tokens.length < context.getExpectedColumnCount() || tokens.length < 4) {
            return false;
        }

        final String actor = tokens[1].trim();
        final String accountType = tokens[4].trim();

        return !"0".equals(actor) && accountType.contains(":");
    }
}

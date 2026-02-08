package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import io.github.finoid.bank.domain.maven.plugin.AccountTypes;
import io.github.finoid.bank.domain.maven.plugin.Type;
import io.github.finoid.bank.domain.maven.plugin.exceptions.ParseException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Converts a delimited string into an {@link AccountTypes} instance.
 * Expected format: {@code startIndex<delimiter>endIndex" (e.g., "1-2")}.
 */
@Singleton
@Named("accountTypes")
public class AccountTypesConverter implements Converter<String, AccountTypes> {
    @Override
    public AccountTypes convert(@Nullable String value, final ConverterContext converterContext) {
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Account types value is missing or empty");
        }

        try {
            final String delimiter = converterContext.getNoneEmptyOrThrow("delimiter");

            final List<String> parts = Splitter.onPattern(delimiter).splitToList(value);
            if (parts.size() != 2) {
                throw new ParseException("Expected two parts for account types, but got: '" + value + "'");
            }

            final int start = Integer.parseInt(parts.get(0).trim());
            final int end = Integer.parseInt(parts.get(1).trim());

            return new AccountTypes(Type.ofIndice(start), Type.ofIndice(end));
        } catch (final NumberFormatException e) {
            throw new ParseException("Invalid number format in account types: '" + value + "'", e);
        }
    }
}

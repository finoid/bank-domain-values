package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import io.github.finoid.bank.domain.maven.plugin.Actor;
import io.github.finoid.bank.domain.maven.plugin.exceptions.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.component.annotations.Component;
import org.jspecify.annotations.Nullable;

import javax.inject.Singleton;
import java.util.Locale;

/**
 * Converts a string into an {@link Actor} instance.
 */
@Singleton
@Component(role = Converter.class, hint = "actor")
public class ActorConverter implements Converter<String, Actor> {
    @Override
    public Actor convert(@Nullable String value, final ConverterContext converterContext) {
        if (StringUtils.isBlank(value)) {
            throw new ParseException("Account types value is missing or empty");
        }

        return Actor.ofNameAndNormalizedName(value.trim(), normalizeName(value));
    }

    private static String normalizeName(final String name) {
        return name.trim()
            .toUpperCase(Locale.getDefault())
            // Normalize special characters
            .replace("&", "AND")
            .replace("Ö", "O")
            .replace("Å", "A")
            .replace("Ä", "A")
            .replace("É", "E")
            // Replace separators
            .replace("-", "_")
            .replaceAll("\\s+", "_")
            // Strip all remaining non-alphanumeric characters (except underscores)
            .replaceAll("[^A-Z0-9_]", "");
    }
}

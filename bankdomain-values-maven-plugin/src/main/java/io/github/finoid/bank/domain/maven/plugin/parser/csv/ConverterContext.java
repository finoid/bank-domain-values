package io.github.finoid.bank.domain.maven.plugin.parser.csv;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Represents a context container for passing metadata into a {@link Converter}.
 * <p>
 * This is typically used to supply additional parameters (such as delimiters, formats, etc.)
 * when parsing CSV values via {@link ColumnValueConverter}.
 *
 * <p>Use {@code #ofMetadata(Map)} to construct an instance.</p>
 */
@Value(staticConstructor = "ofMetadata")
public class ConverterContext {
    Map<String, String> metadata;

    public String getNoneEmptyOrThrow(final String key) {
        if (!metadata.containsKey(key)) {
            throw new IllegalArgumentException("No metadata for key " + key);
        }

        if (StringUtils.isBlank(metadata.get(key))) {
            throw new IllegalArgumentException("Metadata for key " + key + " is empty");
        }

        return metadata.get(key);
    }
}

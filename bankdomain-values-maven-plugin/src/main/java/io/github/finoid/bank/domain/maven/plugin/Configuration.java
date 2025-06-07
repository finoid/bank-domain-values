package io.github.finoid.bank.domain.maven.plugin;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;
import org.jspecify.annotations.Nullable;

@Data
public class Configuration {
    /**
     * Whether the checkstyle analyzer should be enabled or disabled.
     */
    @Parameter(property = "bd.enabled")
    private boolean enabled = true;

    @Parameter(property = "bd.sourceRoot", defaultValue = "${project.build.directory}/generated-sources")
    private String sourceRoot;

    @Nullable
    @Parameter(property = "bd.csvFilePath")
    private String csvFilePath;

    public boolean hasCsvFilePath() {
        return csvFilePath != null && !csvFilePath.isEmpty();
    }
}

package io.github.finoid.bank.domain.maven.plugin;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

@Data
public class Configuration {
    /**
     * Whether the checkstyle analyzer should be enabled or disabled.
     */
    @Parameter(property = "bd.enabled")
    private boolean enabled = true;

    @Parameter(property = "bd.sourceRoot", defaultValue = "${project.build.directory}/generated-sources")
    private String sourceRoot;

    @Parameter(property = "bd.sourceRoot", defaultValue = "clearingnummertabell-for-nedladdning.csv")
    private String csvFilePath = "clearingnummertabell-for-nedladdning.csv";
}

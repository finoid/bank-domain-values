package io.github.finoid.bank.domain.maven.plugin;

import com.google.errorprone.annotations.MustBeClosed;
import io.github.finoid.bank.domain.maven.plugin.codegen.BankEnumGenerator;
import io.github.finoid.bank.domain.maven.plugin.exceptions.BankDomainMavenPluginException;
import io.github.finoid.bank.domain.maven.plugin.parser.Parser;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.BankAccountLineValidator;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.LineParserContext;
import io.github.finoid.bank.domain.maven.plugin.util.ObjectUtils;
import io.github.finoid.bank.domain.maven.plugin.util.Precondition;
import io.github.finoid.bank.domain.maven.plugin.util.ResourceUtils;
import lombok.SneakyThrows;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maven plugin for parsing a CSV-based domain model and generating Java enum classes.
 */
@Mojo(name = "code-generation", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CodeGeneration extends AbstractMojo {
    private static final String DEFAULT_CSV_RESOURCE = "clearingnummertabell-for-nedladdning.csv";

    @Parameter(alias = "codeQuality")
    private final Configuration configuration;
    private final MavenSession mavenSession;
    private final MavenProject mavenProject;
    private final BankEnumGenerator bankEnumGenerator;
    private final Parser<ActorAccountContext> parser;

    @Inject
    public CodeGeneration(final Configuration configuration,
                          final MavenSession mavenSession,
                          final MavenProject mavenProject,
                          final BankEnumGenerator bankEnumGenerator,
                          final Parser<ActorAccountContext> parser) {
        this.configuration = Precondition.nonNull(configuration, "Configuration must not be null.");
        this.mavenSession = Precondition.nonNull(mavenSession, "MavenSession must not be null.");
        this.mavenProject = Precondition.nonNull(mavenProject, "MavenProject must not be null.");
        this.bankEnumGenerator = Precondition.nonNull(bankEnumGenerator, "BankEnumGenerator must not be null.");
        this.parser = Precondition.nonNull(parser, "Parser must not be null.");
    }

    @Override
    @SneakyThrows
    public void execute() {
        final String sourceRoot = ObjectUtils.valueOrFallback(configuration.getSourceRoot(),
            () -> mavenSession.getCurrentProject().getBuild().getDirectory() + "/generated-sources");

        final Path outputDirectory = Paths.get(sourceRoot);

        final LineParserContext<ActorAccountContext> context = LineParserContext.<ActorAccountContext>builder()
            .delimiter(";")
            .expectedColumnCount(6)
            .lineType(ActorAccountContext.class)
            .skipFirstLine(true)
            .lineValidator(new BankAccountLineValidator())
            .build();

        try (final InputStream inputStream = csvFileInputStream()) {
            final Map<Actor, List<ActorAccountContext>> grouped = parser.parse(inputStream, context)
                .collect(Collectors.groupingBy(ActorAccountContext::getActor, LinkedHashMap::new, Collectors.toList()));

            bankEnumGenerator.generate(grouped, outputDirectory);
        } catch (final IOException e) {
            throw new BankDomainMavenPluginException("Unable to access the csv file. Cause: " + e.getMessage(), e);
        }

        mavenProject.addCompileSourceRoot(outputDirectory.toAbsolutePath().toString());
    }

    @MustBeClosed
    private InputStream csvFileInputStream() throws IOException {
        if (configuration.hasCsvFilePath()) {
            //noinspection DataFlowIssue
            return ResourceUtils.tryInputStreamFrom(Paths.get(configuration.getCsvFilePath()));
        }

        return ResourceUtils.tryInputStreamFrom(DEFAULT_CSV_RESOURCE);
    }
}

package io.github.finoid.bank.domain.maven.plugin;

import com.google.errorprone.annotations.MustBeClosed;
import io.github.finoid.bank.domain.maven.plugin.codegen.BankEnumGenerator;
import io.github.finoid.bank.domain.maven.plugin.exceptions.BankDomainMavenPluginException;
import io.github.finoid.bank.domain.maven.plugin.parser.Parser;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.AccountTypesConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.ActorConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.BankAccountLineValidator;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.CsvParser;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.LineParser;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.LineParserContext;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.NoOpConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.RangeConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.ValueConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.metadata.ClassMetadataReader;
import io.github.finoid.bank.domain.maven.plugin.util.ObjectUtils;
import io.github.finoid.bank.domain.maven.plugin.util.Precondition;
import io.github.finoid.bank.domain.maven.plugin.util.ResourceUtils;
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

    @Inject
    public CodeGeneration(final Configuration configuration, final MavenSession mavenSession, final MavenProject mavenProject) {
        this.configuration = Precondition.nonNull(configuration, "Configuration shouldn't be null");
        this.mavenSession = Precondition.nonNull(mavenSession, "MavenSession shouldn't be null");
        this.mavenProject = Precondition.nonNull(mavenProject, "MavenProject shouldn't be null");
    }

    @Override
    public void execute() {
        final ValueConverter valueConverter = new ValueConverter(Map.of(
            RangeConverter.class, new RangeConverter(),
            NoOpConverter.class, new NoOpConverter(),
            ActorConverter.class, new ActorConverter(),
            AccountTypesConverter.class, new AccountTypesConverter()
        ));

        final LineParser lineParser = new LineParser(valueConverter, new ClassMetadataReader());

        final LineParserContext<ActorAccountContext> context = LineParserContext.<ActorAccountContext>builder()
            .delimiter(";")
            .expectedColumnCount(6)
            .lineType(ActorAccountContext.class)
            .skipFirstLine(true)
            .lineValidator(new BankAccountLineValidator())
            .build();

        final Parser<ActorAccountContext> parser = new CsvParser<>(lineParser);

        final BankEnumGenerator generator = new BankEnumGenerator();

        final String sourceRoot = ObjectUtils.valueOrFallback(configuration.getSourceRoot(),
            () -> mavenSession.getCurrentProject().getBuild().getDirectory() + "/generated-sources");

        final Path outputDirectory = Paths.get(sourceRoot);

        try (final InputStream inputStream = csvFileInputStream()) {
            final Map<Actor, List<ActorAccountContext>> grouped = parser.parse(inputStream, context)
                .collect(Collectors.groupingBy(ActorAccountContext::getActor, LinkedHashMap::new, Collectors.toList()));

            generator.generate(grouped, outputDirectory);
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

package io.github.finoid.bank.domain.maven.plugin;

import io.github.finoid.bank.domain.maven.plugin.codegen.BankEnumGenerator;
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
import lombok.SneakyThrows;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedVariable")
@Mojo(name = "code-generation", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CodeGeneration extends AbstractMojo {
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
    @SneakyThrows
    @SuppressWarnings("Checkstyle")
    public void execute() {
        // TODO (nw) maven plugin configuration, and should be derived from class path?
        final Path csvPath = Paths.get("/Users/Nicklas/Downloads/clearingnummertabell-for-nedladdning.csv");

        System.out.println("CSV file " + configuration.getCsvFilePath());

        // TODO (nw) use file full path, or grab from class path?



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

        try (final InputStream inputStream = new FileInputStream(csvPath.toFile())) {
            final Map<Actor, List<ActorAccountContext>> grouped = parser.parse(inputStream, context)
                .collect(Collectors.groupingBy(ActorAccountContext::getActor, LinkedHashMap::new, Collectors.toList()));

            generator.generate(grouped, outputDirectory);
        }

        mavenProject.addCompileSourceRoot(outputDirectory.toAbsolutePath().toString());
    }
}

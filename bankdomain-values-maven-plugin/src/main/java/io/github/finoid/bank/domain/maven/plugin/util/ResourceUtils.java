package io.github.finoid.bank.domain.maven.plugin.util;

import com.google.errorprone.annotations.MustBeClosed;
import io.github.finoid.bank.domain.maven.plugin.exceptions.BankDomainMavenPluginException;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class ResourceUtils {

    /**
     * Attempts to retrieve an {@link InputStream} from the classpath using the provided resource name.
     * <p>
     * This is useful for loading embedded resources bundled within the plugin's JAR.
     *
     * @param resourceName the name of the resource to load from the classpath (e.g., "templates/myfile.txt")
     * @return an open {@link InputStream} to the requested resource
     * @throws IllegalArgumentException       if {@code resourceName} is null
     * @throws BankDomainMavenPluginException if the resource is not found or access is restricted
     */
    @MustBeClosed
    public static InputStream tryInputStreamFrom(final String resourceName) {
        Precondition.nonNull(resourceName);

        @Nullable final InputStream resourceAsStream;
        try {
            resourceAsStream = classLoader().getResourceAsStream(resourceName);

            if (resourceAsStream == null) {
                throw new BankDomainMavenPluginException(
                    String.format("The resource could not found and/or accessed. %s", resourceName));
            }
        } catch (SecurityException | IllegalStateException | Error e) {
            throw new BankDomainMavenPluginException("Unable to retrieve input stream from " + resourceName, e);
        }

        return resourceAsStream;
    }

    /**
     * Attempts to retrieve an {@link InputStream} from the given file system path.
     * <p>
     * This is useful for user-specified files passed via plugin configuration.
     *
     * @param path the file system path to load
     * @return an open {@link InputStream} to the file
     * @throws IllegalArgumentException       if {@code path} is null
     * @throws BankDomainMavenPluginException if the file doesn't exist or cannot be opened
     */
    @MustBeClosed
    public static InputStream tryInputStreamFrom(final Path path) {
        Precondition.nonNull(path);

        if (!Files.isReadable(path)) {
            throw new BankDomainMavenPluginException("File is not readable: " + path.toAbsolutePath());
        }

        final File file = path.toFile();

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new BankDomainMavenPluginException("Error reading file", e);
        }
    }

    private static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}

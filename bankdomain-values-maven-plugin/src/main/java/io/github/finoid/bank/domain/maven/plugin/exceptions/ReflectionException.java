package io.github.finoid.bank.domain.maven.plugin.exceptions;

public class ReflectionException extends BankDomainMavenPluginException {
    public ReflectionException(final String message) {
        super(message);
    }

    public ReflectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

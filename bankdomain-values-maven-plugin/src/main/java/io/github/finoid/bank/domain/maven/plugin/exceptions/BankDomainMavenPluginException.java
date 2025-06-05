package io.github.finoid.bank.domain.maven.plugin.exceptions;

public class BankDomainMavenPluginException extends RuntimeException {
    public BankDomainMavenPluginException(final String message) {
        super(message);
    }

    public BankDomainMavenPluginException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

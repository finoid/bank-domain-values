package io.github.finoid.bank.domain.maven.plugin.exceptions;

public class ParseException extends BankDomainMavenPluginException {
    public ParseException(final String message) {
        super(message);
    }

    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

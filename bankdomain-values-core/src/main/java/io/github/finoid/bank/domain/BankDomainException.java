package io.github.finoid.bank.domain;

import org.jspecify.annotations.Nullable;

public class BankDomainException extends RuntimeException {
    public BankDomainException(final String message) {
        super(message);
    }

    public BankDomainException(final String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

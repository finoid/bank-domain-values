package io.github.finoid.bank.domain;

import org.jspecify.annotations.Nullable;

public class IllegalNumberBankDomainException extends BankDomainException {
    // TODO (nw) illegal number value?

    public IllegalNumberBankDomainException(final String message) {
        super(message);
    }

    public IllegalNumberBankDomainException(final String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

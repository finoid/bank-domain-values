package io.github.finoid.bank.domain;

import lombok.Value;

/**
 * Value object representing a Swedish bank account number without clearing number.
 * <p>
 * Provides validation and safe construction from both {@code long} and {@code String} inputs.
 */
@Value
public class AccountNumber {
    String number; // TODO (nw) use string instead

    private AccountNumber(final String number) {
        this.number = number;

        validateSelf();
    }

    /**
     * Creates an {@code AccountNumber} from a {@code long} value.
     *
     * @param number the numeric account number
     * @return a new {@code AccountNumber} instance
     * @throws IllegalNumberBankDomainException if the number is invalid
     */
    public static AccountNumber ofNumber(final long number) {
        return new AccountNumber(String.valueOf(number));
    }

    /**
     * Creates an {@code AccountNumber} from a {@code long} value.
     *
     * @param input the string representation of the account input
     * @return a new {@code AccountNumber} instance
     * @throws IllegalNumberBankDomainException if the input is invalid
     */
    public static AccountNumber ofString(final String input) {
        return new AccountNumber(input);
    }

    private void validateSelf() {
        if (number.length() < 2) { // TODO (nw) use the validation information from the bank type?
            throw new IllegalNumberBankDomainException("Account number must be at least 2 digits long");
        }
    }

    @Override
    public String toString() { // TODO (nw) use format instead?
        return number;
    }
}

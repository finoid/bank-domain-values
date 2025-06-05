package io.github.finoid.bank.domain;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Value object representing a Swedish bank account number with clearing number.
 * <p>
 * A valid {@code BankAccountNumber} consists of:
 * <ul>
 *     <li>A {@link ClearingNumber} – 4 or 5 digits, potentially with a sort digit</li>
 *     <li>An {@link AccountNumber} – the actual account number</li>
 *     <li>A resolved {@link BankAndType} – providing metadata about the bank and validation rules</li>
 * </ul>
 */
@Value
public class BankAccountNumber {
    ClearingNumber clearingNumber;
    AccountNumber accountNumber;
    BankAndType bankAndType;

    private BankAccountNumber(final ClearingNumber clearingNumber, final AccountNumber accountNumber, final BankAndType bankAndType) {
        this.clearingNumber = clearingNumber;
        this.accountNumber = accountNumber;
        this.bankAndType = bankAndType;

        validateSelf();
    }

    /**
     * Returns {@code true} if the given input is a valid bank account number.
     *
     * @param accountNumber the input to validate
     * @return {@code true} if the input is valid; {@code false} otherwise
     */
    public static boolean isValid(final String accountNumber) {
        try {
            ofString(accountNumber);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Creates a {@code BankAccountNumber} from a {@code Long}.
     *
     * @param accountNumber a numeric representation of the account
     * @return a new {@code BankAccountNumber} instance
     */
    public static BankAccountNumber ofNumber(final Long accountNumber) {
        return Factory.fromLong(accountNumber);
    }

    /**
     * Creates a {@code BankAccountNumber} from a {@code String}.
     *
     * @param input string that may include spaces, hyphens, etc.
     * @return a new {@code BankAccountNumber} instance
     */
    public static BankAccountNumber ofString(final String input) {
        return Factory.fromString(input);
    }

    /**
     * Creates a {@code BankAccountNumber} from its numeric clearing and account parts.
     *
     * @param clearing 4- or 5-digit clearing number
     * @param account  account number (should not contain the clearing number part)
     * @return a new {@code BankAccountNumber} instance
     */
    public static BankAccountNumber ofClearingAndAccountNumber(final int clearing, final int account) {
        return Factory.fromNumbers(clearing, account);
    }

    /**
     * Returns a formatted string representation of the account number.
     * Format: {@code <clearingNumber>-<zero-padded accountNumber>}.
     *
     * @return the formatted account string
     */
    public String toFormatted(final BankAccountFormatter.Format format) {
        return BankAccountFormatter.format(this, format);
    }

    private void validateSelf() {
        Validator.validate(clearingNumber, accountNumber, bankAndType);
    }

    private static class Factory {
        private static final Pattern NON_DIGITS_PATTERN = Pattern.compile("\\D");

        private Factory() {
        }

        /**
         * Creates a {@link BankAccountNumber} from a formatted or unformatted string.
         *
         * @param rawInput account number string containing digits, spaces, dashes, etc.
         * @return a valid {@link BankAccountNumber}
         * @throws IllegalNumberBankDomainException if the input is invalid
         */
        public static BankAccountNumber fromString(String rawInput) {
            final String digitsOnly = NON_DIGITS_PATTERN.matcher(rawInput).replaceAll("");

            if (digitsOnly.length() < 5) {
                throw new IllegalNumberBankDomainException("Account number is too short: " + rawInput);
            }

            int clearingLength = digitsOnly.startsWith("8") ? 5 : 4;

            if (digitsOnly.length() <= clearingLength) {
                throw new IllegalNumberBankDomainException("Missing account number after clearing number: " + rawInput);
            }

            final ClearingNumber clearingNumber = ClearingNumber.ofString(digitsOnly.substring(0, clearingLength));
            final AccountNumber accountNumber = AccountNumber.ofString(digitsOnly.substring(clearingLength));

            final BankAndType bankAndType = BankAndType.findByClearingNumber(clearingNumber)
                .orElseThrow(() -> new IllegalNumberBankDomainException("Unknown clearing number: " + clearingNumber));

            return new BankAccountNumber(clearingNumber, accountNumber, bankAndType);
        }

        /**
         * Creates a {@link BankAccountNumber} from numeric clearing and account number parts.
         *
         * @param clearing 4- or 5-digit clearing number
         * @param account  account number (should not contain the clearing number part)
         * @return a valid {@link BankAccountNumber}
         * @throws IllegalNumberBankDomainException if the combination is invalid
         */
        public static BankAccountNumber fromNumbers(int clearing, int account) {
            final ClearingNumber clearingNumber = ClearingNumber.ofNumber(clearing);
            final AccountNumber accountNumber = AccountNumber.ofNumber(account);

            final BankAndType bankAndType = BankAndType.findByClearingNumber(clearingNumber)
                .orElseThrow(() -> new IllegalNumberBankDomainException(
                    "Could not resolve bank for clearing number: " + clearing
                ));

            return new BankAccountNumber(clearingNumber, accountNumber, bankAndType);
        }

        /**
         * Creates a {@link BankAccountNumber} from a {@code long} representation.
         *
         * @param fullNumber a number containing both clearing and account digits
         * @return a valid {@link BankAccountNumber}
         */
        public static BankAccountNumber fromLong(long fullNumber) {
            return fromString(Long.toString(fullNumber));
        }
    }

    private static class Validator {
        private Validator() {
        }

        @SuppressWarnings("OperatorPrecedence")
        public static void validate(final ClearingNumber clearingNumber, final AccountNumber accountNumber, final BankAndType bankAndType) {
            final BankAccountType bankAccountType = bankAndType.getBankType().getType();
            final BankAccountSubType bankAccountSubType = bankAndType.getBankType().getSubType();

            // 1:1
            if (bankAndType.isOfType(BankAccountType.ONE, BankAccountSubType.ONE)) {
                final String toBeValidated = clearingNumber.toFormatted().substring(1) + StringUtils.leftPad(accountNumber.toString(), 7, '0');
                assertMod11(toBeValidated);
            }

            // 1:2
            if (bankAndType.isOfType(BankAccountType.ONE, BankAccountSubType.TWO)) {
                final String toBeValidated = clearingNumber + StringUtils.leftPad(accountNumber.toString(), 7, '0');
                assertMod11(toBeValidated);
            }

            // 2:2
            if (bankAccountType == BankAccountType.TWO && bankAccountSubType == BankAccountSubType.TWO) {
                final String toBeValidated = StringUtils.leftPad(accountNumber.toString(), 9, '0');
                assertMod11(toBeValidated);
            }

            // 2:1, 2:3
            if (bankAccountType == BankAccountType.TWO && bankAccountSubType == BankAccountSubType.ONE
                || bankAccountType == BankAccountType.TWO && bankAccountSubType == BankAccountSubType.THREE) {
                final String toBeValidated = StringUtils.leftPad(accountNumber.toString(), 10, '0');
                assertMod10(toBeValidated);
            }

            // 2:4
            if (bankAccountType == BankAccountType.TWO && bankAccountSubType == BankAccountSubType.FOUR) {
                final String toBeValidated = StringUtils.leftPad(accountNumber.toString(), 10, '0');
                assertMod11(toBeValidated);
            }
        }

        private static void assertMod10(final String toBeValidated) {
            if (!MathUtils.isMod10(toBeValidated)) {
                throw new IllegalNumberBankDomainException("Invalid clearing and/or account number. Mod11 check failed: " + toBeValidated);
            }
        }

        private static void assertMod11(final String toBeValidated) {
            if (!MathUtils.isMod11(toBeValidated)) {
                throw new IllegalNumberBankDomainException("Invalid clearing and/or account number. Mod11 check failed: " + toBeValidated);
            }
        }
    }
}

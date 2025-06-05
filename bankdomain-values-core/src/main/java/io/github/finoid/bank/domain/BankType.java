package io.github.finoid.bank.domain;

import lombok.Value;

import java.util.Set;

/**
 * Represents a specific bank account type configuration, used to determine
 * validation behavior and clearing number ranges.
 * <p>
 * A {@code BankType} combines:
 * <ul>
 *   <li>{@link BankAccountType} – the top-level classification of the account type</li>
 *   <li>{@link BankAccountSubType} – a more specific subtype, used for detailed validation</li>
 *   <li>A set of valid clearing number ranges, used to match accounts to this type</li>
 * </ul>
 */
@Value
public class BankType {
    private static final int DEFAULT_ACCOUNT_LENGTH = 11;

    BankAccountType type;
    BankAccountSubType subType;
    int accountMinLength;
    int accountMaxLength;
    Set<IntRanges> checkNumberRanges;

    /**
     * Creates a {@code BankType} with specified type, subtype, and one or more clearing number ranges.
     *
     * @param type    the main type classification
     * @param subType the subtype classification
     * @param ranges  one or more clearing number ranges associated with this type
     * @return a new {@code BankType} instance
     */
    public static BankType ofTypesAndRanges(final BankAccountType type, final BankAccountSubType subType, final IntRanges... ranges) {
        return new BankType(type, subType, DEFAULT_ACCOUNT_LENGTH, DEFAULT_ACCOUNT_LENGTH, Set.of(ranges));
    }

    /**
     * Creates a {@code BankType} with specified type, subtype, and one or more clearing number ranges.
     *
     * @param type             the main type classification
     * @param subType          the subtype classification
     * @param accountMinLength the account min length
     * @param accountMaxLength the account max length
     * @param ranges           one or more clearing number ranges associated with this type
     * @return a new {@code BankType} instance
     */
    public static BankType ofTypesAndRanges(final BankAccountType type, final BankAccountSubType subType, final int accountMinLength,
                                            final int accountMaxLength, final IntRanges... ranges) {
        return new BankType(type, subType, accountMinLength, accountMaxLength, Set.of(ranges));
    }

    /**
     * Checks if the given clearing number falls within any of this bank type's configured ranges.
     *
     * @param number the clearing number to evaluate
     * @return {@code true} if the number matches one of the ranges; {@code false} otherwise
     */
    public boolean isWithinRange(final int number) {
        return checkNumberRanges.stream()
            .anyMatch(range -> range.isWithinRange(number));
    }
}

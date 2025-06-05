package io.github.finoid.bank.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Value object representing a Swedish clearing number, optionally with a sorting digit.
 * <p>
 * A valid clearing number must:
 * <ul>
 *   <li>Be at least 4 digits long</li>
 *   <li>Optionally include a 5th digit (sorting number)</li>
 *   <li>Pass a mod10 validation if a sorting number is included</li>
 * </ul>
 */
@Value
public class ClearingNumber {
    /**
     * The main 4-digit clearing number.
     */
    int clearingNumber;

    /**
     * Optional sorting digit (5th digit), only present if the original input is 5 digits.
     */
    @Nullable
    @Getter(AccessLevel.NONE)
    Integer sortingNumber;

    private ClearingNumber(final int clearingNumber, @Nullable final Integer sortingNumber) {
        this.clearingNumber = clearingNumber;
        this.sortingNumber = sortingNumber;

        validateSelf();
    }

    /**
     * Creates a {@code ClearingNumber} from a {@code int} input.
     * If the number is 5 digits, the last digit is treated as the sorting number.
     *
     * @param number a 4- or 5-digit number
     * @return a new {@code ClearingNumber} instance
     * @throws IllegalNumberBankDomainException if the number is invalid
     */
    public static ClearingNumber ofNumber(final int number) {
        return Factory.parse(number);
    }

    /**
     * Creates a {@code ClearingNumber} from a {@code int} input.
     *
     * @param input a numeric string
     * @return a new {@code ClearingNumber} instance
     * @throws IllegalNumberBankDomainException if the input is invalid
     */
    public static ClearingNumber ofString(final String input) {
        try {
            return Factory.parse(Integer.parseInt(input));
        } catch (final NumberFormatException e) {
            throw new IllegalNumberBankDomainException("Invalid clearing input. Number: " + input, e);
        }
    }

    /**
     * Returns the optional sorting number (if present).
     *
     * @return an {@code Optional} containing the sorting number, or empty if not set
     */
    public Optional<Integer> optionalSortingNumber() {
        return Optional.ofNullable(sortingNumber);
    }

    /**
     * Returns {@code true} if a sorting number is present, {@code false} otherwise.
     *
     * @return an {@code true} if a sorting number is present, @code false} otherwise.
     */
    public boolean hasSortingNumber() {
        return sortingNumber != null;
    }

    /**
     * Returns the full clearing number, combining the main number and sorting number if present.
     *
     * @return a combined 4- or 5-digit clearing number
     */
    public int toInt() {
        if (sortingNumber == null) {
            return clearingNumber;
        }

        return clearingNumber * 10 + sortingNumber;
    }

    @Override
    public String toString() {
        return sortingNumber != null
            ? String.format("%04d%d", clearingNumber, sortingNumber)
            : String.format("%04d", clearingNumber);
    }

    public String toFormatted() {
        return sortingNumber != null
            ? String.format("%04d-%d", clearingNumber, sortingNumber)
            : String.format("%04d", clearingNumber);
    }

    private void validateSelf() {
        if (clearingNumber < 1000) {
            throw new IllegalNumberBankDomainException("Clearing number must be at least 4 digits long");
        }

        int clearingNumber = toInt(); // TODO (nw) should include sorting number?

        if (clearingNumber > 9999 && !MathUtils.isMod10(clearingNumber)) {
            throw new IllegalNumberBankDomainException("Clearing number containing a sort key must be mod10");
        }
    }

    private static class Factory {
        public static ClearingNumber parse(int number) {
            if (number >= 10_000) {
                return new ClearingNumber(number / 10, number % 10);
            }

            return new ClearingNumber(number, null);
        }
    }
}

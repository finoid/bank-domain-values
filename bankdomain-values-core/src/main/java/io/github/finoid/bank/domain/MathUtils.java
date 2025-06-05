package io.github.finoid.bank.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
class MathUtils {
    /**
     * Validates a numeric string using the Luhn (modulus 10) algorithm.
     * <p>
     * This algorithm is widely used for validating identification numbers such as
     * credit card numbers and some Swedish account numbers.
     *
     * @param toBeValidated the numeric value to validate
     * @return {@code true} if the toBeValidated passes the mod10 check; {@code false} otherwise
     */
    public static boolean isMod10(final String toBeValidated) {
        long number;
        try {
            number = Long.parseLong(toBeValidated);
        } catch (final NumberFormatException e) {
            return false;
        }

        return isMod10(number);
    }

    /**
     * Validates a numeric using the Luhn (modulus 10) algorithm.
     * <p>
     * This algorithm is widely used for validating identification numbers such as
     * credit card numbers and some Swedish account numbers.
     *
     * @param toBeValidated the numeric value to validate
     * @return {@code true} if the toBeValidated passes the mod10 check; {@code false} otherwise
     */
    public static boolean isMod10(long toBeValidated) {
        int bit = 1;
        int sum = 0;
        final int[] arr = {0, 2, 4, 6, 8, 1, 3, 5, 7, 9};

        while (toBeValidated > 0) {
            int digit = (int) (toBeValidated % 10);
            bit ^= 1;
            sum += bit == 1 ? arr[digit] : digit;
            toBeValidated /= 10;
        }

        return sum != 0 && sum % 10 == 0;
    }

    /**
     * Validates a numeric string using the Modulus 11 algorithm.
     * <p>
     * This implementation uses a custom weight sequence and expects the input string
     * to consist only of digits. It is commonly used for bank account reference numbers.
     *
     * @param toBeValidated the toBeValidated as a string of digits
     * @return {@code true} if the toBeValidated passes the mod11 check; {@code false} otherwise
     * @throws NumberFormatException if the input contains non-digit characters
     */
    public static boolean isMod11(final String toBeValidated) {
        if (toBeValidated == null || toBeValidated.length() < 2 || !toBeValidated.matches("\\d+")) {
            throw new IllegalArgumentException("Input must be a numeric string with at least two digits");
        }

        int weight = 2;
        int sum = 0;

        // Exclude the last digit (assumed to be the check digit)
        for (int i = toBeValidated.length() - 2; i >= 0; i--) {
            int digit = toBeValidated.charAt(i) - '0';
            sum += digit * weight;
            weight = (weight < 10) ? weight + 1 : 1;
        }

        int remainder = sum % 11;
        int expectedCheckDigit = 11 - remainder;

        if (expectedCheckDigit == 11) {
            expectedCheckDigit = 0;
        } else if (expectedCheckDigit == 10) {
            return false; // check digit cannot be 10
        }

        int actualCheckDigit = toBeValidated.charAt(toBeValidated.length() - 1) - '0';

        return actualCheckDigit == expectedCheckDigit;
    }
}

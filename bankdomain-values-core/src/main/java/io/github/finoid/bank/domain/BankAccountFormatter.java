package io.github.finoid.bank.domain;

import io.github.finoid.generated.bank.domain.Bank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankAccountFormatter {
    /**
     * Formats a bank account number based on bank name, type, and desired format style.
     *
     * @param bankAccountNumber a Swedish bank account number with clearing number.
     * @param format            the desired format style, either {@link Format#PRETTY} or {@link Format#DEFAULT}.
     * @return formatted string
     */
    public static String format(final BankAccountNumber bankAccountNumber, final Format format) {
        final String clearingNumber = String.valueOf(bankAccountNumber.getClearingNumber().toInt());
        final String accountNumber = bankAccountNumber.getAccountNumber().toString();

        final BankType bankType = bankAccountNumber.getBankAndType()
            .getBankType();

        final Bank bank = bankAccountNumber.getBankAndType()
            .getBank();

        final BankAccountType type = bankType.getType();

        if (format == Format.PRETTY) {
            return switch (bank) {
                case NORDEA, NORDEA_PLUSGIROT -> formatter(clearingNumber, accountNumber, "CCCC,AAAAAA-AAAA", false);
                case HANDELSBANKEN -> formatter(clearingNumber, accountNumber, "CCCC,AAA AAA AAA", false);
                case SWEDBANK -> {
                    if (bankAccountNumber.getClearingNumber().hasSortingNumber()) {
                        yield formatter(clearingNumber, accountNumber, "CCCC-C,AAA AAA AAA-A", false);
                    }

                    yield formatter(clearingNumber, accountNumber, "CCCC,AA-AAAAA", true);
                }
                default -> formatter(clearingNumber, accountNumber, "CCCC,AA AAAA AAAA", false);
            };
        }

        final int length = type == BankAccountType.TWO ? bankType.getAccountMaxLength() : 7;
        final String mask = "CCCCC" + "A".repeat(length);

        return formatter(clearingNumber, accountNumber, mask, true);
    }

    /**
     * Applies a format mask to the given sorting code and account number.
     * 'C' is used as a placeholder for clearing number, 'A' for account number digits.
     *
     * @param clearingNumber the sorting code (typically 4–5 digits)
     * @param accountNumber  the account number
     * @param mask           the format mask (e.g., "CCCC-AA-AAAAA")
     * @param pad            whether to pad missing digits with zeros (applies to account number only)
     * @return formatted account string
     */
    private static String formatter(String clearingNumber, String accountNumber, String mask, boolean pad) {
        final int lastS = mask.lastIndexOf('C');
        final String sortingCodeMask = lastS >= 0 ? mask.substring(0, lastS + 1) : "";
        final String accountNumberMask = lastS >= 0 ? mask.substring(lastS + 1) : mask;

        final Deque<Character> sortingCodeChars = toDeque(clearingNumber);
        final Deque<Character> accountNumberChars = toDeque(accountNumber);

        final StringBuilder sortingCodeResult = new StringBuilder();
        final StringBuilder accountNumberResult = new StringBuilder();

        // Fill sorting code left to right
        for (int i = 0; i < sortingCodeMask.length(); i++) {
            char c = sortingCodeMask.charAt(i);
            if (c == 'C') {
                sortingCodeResult.append(safePoll(sortingCodeChars));
            } else {
                sortingCodeResult.append(c);
            }
        }

        // Fill account number right to left
        for (int i = accountNumberMask.length() - 1; i >= 0; i--) {
            char maskChar = accountNumberMask.charAt(i);

            if (maskChar == 'A') {
                char digit = accountNumberChars.isEmpty()
                    ? (pad ? '0' : ' ')
                    : accountNumberChars.pollLast();
                accountNumberResult.insert(0, digit);
            } else {
                accountNumberResult.insert(0, maskChar);
            }

            if (i == accountNumberMask.indexOf('A') && !accountNumberChars.isEmpty()) {
                while (!accountNumberChars.isEmpty()) {
                    accountNumberResult.insert(0, accountNumberChars.pollLast());
                }
            }

            if (accountNumberChars.isEmpty() && !pad) {
                String prefix = accountNumberMask.substring(0, accountNumberMask.indexOf('A'));
                accountNumberResult.insert(0, prefix);
                break;
            }
        }

        return sortingCodeResult.append(accountNumberResult).toString();
    }

    public enum Format {
        PRETTY,
        DEFAULT
    }

    private static Deque<Character> toDeque(final String input) {
        final Deque<Character> deque = new ArrayDeque<>();

        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            deque.add(c);
        }

        return deque;
    }

    private static char safePoll(final Deque<Character> deque) {
        return deque.isEmpty() ? ' ' : deque.poll();
    }
}
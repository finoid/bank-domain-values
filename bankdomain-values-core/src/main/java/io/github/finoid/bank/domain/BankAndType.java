package io.github.finoid.bank.domain;

import lombok.Value;
import io.github.finoid.generated.bank.domain.Bank;

import java.util.Optional;

/**
 * Represents a pairing of a {@link Bank} and its specific {@link BankType} configuration,
 * derived from a clearing number range.
 */
@Value
public class BankAndType {
    Bank bank;
    BankType bankType;

    public boolean isOfType(final BankAccountType bankAccountType, final BankAccountSubType bankAccountSubType) {
        return bankAccountType == bankType.getType()
               && bankAccountSubType == bankType.getSubType();
    }

    /**
     * Attempts to resolve a {@code BankAndType} from the given {@link ClearingNumber}.
     *
     * @param clearingNumber the clearing number to match
     * @return an {@code Optional} containing the matching {@code BankAndType}, or empty if none match
     */
    public static Optional<BankAndType> findByClearingNumber(final ClearingNumber clearingNumber) {
        for (final Bank bank : Bank.values()) {
            for (final BankType type : bank.getTypes()) {
                if (type.isWithinRange(clearingNumber.getClearingNumber())) {
                    return Optional.of(new BankAndType(bank, type));
                }
            }
        }
        return Optional.empty();
    }
}

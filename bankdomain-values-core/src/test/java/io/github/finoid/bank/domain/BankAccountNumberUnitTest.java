package io.github.finoid.bank.domain;

import io.github.finoid.generated.bank.domain.Bank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class BankAccountNumberUnitTest {
    @ParameterizedTest
    @CsvFileSource(resources = "/valid-accounts.csv", delimiter = ';')
    void givenBankAccountNumber_whenOfString_thenExpectedBankAccountNumberReturned(final String accountNumber, final String bankName) {
        var account = BankAccountNumber.ofString(accountNumber);

        Assertions.assertEquals(Bank.valueOf(bankName), account.getBankAndType().getBank());

        // snapshotScenario(account, "account_" + accountNumber + "_bank_" + bankName);
    }
}
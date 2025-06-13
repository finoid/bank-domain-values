package io.github.finoid.bank.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class BankAccountFormatterUnitTest {
    @Test
    void givenSwedbankAccountNumberWithSortingNumber_whenFormat_thenExpectedFormattedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("8129-9,043 386 711-6");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.PRETTY);

        Assertions.assertEquals("8129-9,043 386 711-6", result);
    }

    @Test
    void givenSwedbankAccountNumber_whenFormat_thenExpectedFormattedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("9340 321 4681");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.PRETTY);

        Assertions.assertEquals("9340,32-14681", result);
    }

    @Test
    void givenNordeaPersonalNumberAccountNumber_whenFormat_thenExpectedFormattedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("33006001010328");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.PRETTY);

        Assertions.assertEquals("3300,600101-0328", result);
    }

    @Test
    void givenNordeaAccountNumber_whenFormat_thenExpectedFormattedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("95303648748");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.PRETTY);

        Assertions.assertEquals("9530,364-8748", result);
    }

    @Test
    void givenSwedbankAccountNumberWithSortingNumber_whenFormatDefault_thenExpectedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("8129-9,043 386 711-6");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.DEFAULT);

        Assertions.assertEquals("8129900433867116", result);
    }

    @Test
    void givenSwedbankAccountNumber_whenFormatDefault_thenExpectedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("9340 321 4681");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.DEFAULT);

        Assertions.assertEquals("9340 00003214681", result);
    }

    @Test
    void givenNordeaPersonalNumberAccountNumber_whenFormatDefault_thenExpectedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("33006001010328");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.DEFAULT);

        Assertions.assertEquals("3300 06001010328", result);
    }

    @Test
    void givenNordeaAccountNumber_whenFormatDefault_thenExpectedAccountReturned() {
        final BankAccountNumber accountNumber = BankAccountNumber.ofString("95303648748");

        var result = BankAccountFormatter.format(accountNumber, BankAccountFormatter.Format.DEFAULT);

        Assertions.assertEquals("9530 00003648748", result);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/valid-accounts.csv", delimiter = ';')
    void givenBankAccountNumber_whenOfString_thenExpectedBankAccountNumberReturned(final String accountNumber, final String bankName) {
        var account = BankAccountNumber.ofString(accountNumber);

        var result = BankAccountFormatter.format(account, BankAccountFormatter.Format.PRETTY);

        // snapshotScenario(result, "account_" + accountNumber + "_bank_" + bankName);
    }
}
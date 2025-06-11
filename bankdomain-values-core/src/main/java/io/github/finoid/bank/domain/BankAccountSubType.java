package io.github.finoid.bank.domain;

public enum BankAccountSubType {
    ONE, TWO, THREE, FOUR;

    @SuppressWarnings("EnumOrdinal")
    public int toDigits() {
        return ordinal() + 1;
    }
}

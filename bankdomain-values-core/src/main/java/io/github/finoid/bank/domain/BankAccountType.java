package io.github.finoid.bank.domain;

public enum BankAccountType {
    // 11 digits long sortingCode always included, e.g., SSSSAAAAAAC
    ONE,
    // Type 2 accounts - the messy ones
    TWO;
}

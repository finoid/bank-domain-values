# Finoid Bank Domain Values

Java library for Domain-Driven Design (DDD), providing essential building blocks such as value classes for the bank domain
<div align="center">
  <img src=".github/assets/finoid-bank-domain-values.jpg" width="256">
</div>

## Modules

- **bankdomain-values-core** – Core domain value classes for modeling banking identity concepts in a DDD style.
- **bankdomain-values-maven-plugin** – A Maven plugin that generates the `Bank` enum from a CSV file published by [bankinfrastruktur.se](https://www.bankinfrastruktur.se).
- **bankdomain-values-wasm-graalvm** – A GraalVM WebAssembly module that provides a browser-based validation page.
- **bankdomain-values-wasm-teavm** – A TeaVM WebAssembly module that provides a browser-based validation page.

## What is a domain value class?

A **domain value class** represents a well-defined concept in the domain model with built-in validation and
constraints. Typically immutable, these classes ensure data integrity, enforce domain rules, and increase clarity and maintainability
in your codebase.

## Requirements
- **GraalVM 25** (required for compiling the `bankdomain-values-wasm-graalvm` module)

## Installation

```xml
<dependency>
    <groupId>io.github.finoid</groupId>
    <artifactId>bankdomain-values-core</artifactId>
    <version>${bankdomain-values.version}</version>
</dependency>
```

## API

### Parse and validate from raw input

Accepts spaces, hyphens, and other formatting characters. The input is stripped down to digits internally.

```java
BankAccountNumber account = BankAccountNumber.ofString("8351-9, 392 242 224-5");
```

### From numeric input

```java
BankAccountNumber account = BankAccountNumber.ofNumber(97891111113L);
```

### From clearing and account number

```java
BankAccountNumber account = BankAccountNumber.ofClearingAndAccountNumber(3300, 6205124);
```

### Validation

```java
boolean isValid = BankAccountNumber.isValid("7000-123456789"); // true or false
```

### Formatting

```java
BankAccountNumber account = BankAccountNumber.ofString("83519 3922422245");

account.toFormatted(BankAccountFormatter.Format.PRETTY);  // "8351-9,392 242 224-5"
account.toFormatted(BankAccountFormatter.Format.DEFAULT);  // "835193922422245"
```

### Accessing account components

```java
BankAccountNumber account = BankAccountNumber.ofString("3300 6205124");

// Clearing number
ClearingNumber clearing = account.getClearingNumber();
clearing.getClearingNumber();    // 3300
clearing.toFormatted();          // "3300"
clearing.hasSortingNumber();     // false

// Account number
AccountNumber accountNumber = account.getAccountNumber();
accountNumber.getNumber();       // "6205124"
```

### Bank and account type resolution

The bank and its account type configuration are automatically resolved from the clearing number.

```java
BankAccountNumber account = BankAccountNumber.ofString("3300 6205124");

BankAndType bankAndType = account.getBankAndType();

// Bank information
Bank bank = bankAndType.getBank();
bank.getName();                         // "Nordea"

// Account type information
BankType bankType = bankAndType.getBankType();
bankType.getType();                     // BankAccountType.TWO
bankType.getSubType();                  // BankAccountSubType.ONE
bankType.typesAsString();               // "2:1"
```

### Resolve bank from clearing number

```java
Optional<Bank> bank = Bank.ofClearingNumber(ClearingNumber.ofNumber(5000));
bank.ifPresent(b -> b.getName()); // "SEB"
```

### Error handling

All factory methods throw `BankDomainException` (or its subclass `IllegalNumberBankDomainException`) on invalid input.

```java
try {
    BankAccountNumber.ofString("0000-1234567");
} catch (IllegalNumberBankDomainException e) {
    // "Unknown clearing number: 0000"
}

try {
    BankAccountNumber.ofString("abc");
} catch (IllegalNumberBankDomainException e) {
    // "Account number is too short: abc"
}
```
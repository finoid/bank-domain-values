# Finoid Bank Domain Values

Java library for Domain-Driven Design (DDD), providing essential building blocks such as value classes for the bank domain
<div align="center">
  <img src=".github/assets/finoid-bank-domain-values.jpg" width="256">
</div>

## Modules

- **bankdomain-values-core** – Core domain value classes for modeling banking identity concepts in a DDD style.
- **bankdomain-values-maven-plugin** – A Maven plugin that generates the `Bank` enum from a CSV file published by [bankinfrastruktur.se](https://www.bankinfrastruktur.se).
- **bankdomain-values-wasm** – A WebAssembly module that provides a simple validation page for BankID accounts.

## What is a domain value class?

A **domain value class** represents a well-defined concept in the domain model with built-in validation and
constraints. Typically immutable, these classes ensure data integrity, enforce domain rules, and increase clarity and maintainability
in your codebase.

# API

## Parse and validate from raw input
```java 
BankAccountNumber account = BankAccountNumber.ofString("8351-9, 392 242 224-5");
System.out.println(account.toFormatted(BankAccountFormatter.Format.PRETTY)); // 8351-3922422245
```

## From long or numeric input
```java 
BankAccountNumber account = BankAccountNumber.ofNumber(97891111113L);
```

## From clearing and account number
```java 
BankAccountNumber account = BankAccountNumber.ofClearingAndAccountNumber(3300, 6205124);
```

## Validation only
```java 
boolean isValid = BankAccountNumber.isValid("7000-123456789");
```

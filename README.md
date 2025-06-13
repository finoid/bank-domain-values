# Finoid Bank Domain Values

Java library for Domain-Driven Design (DDD), providing essential building blocks such as value objects for the bank domain
<div align="center">
  <img src=".github/assets/finoid-bank-domain-values.jpg" width="256">
</div>

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

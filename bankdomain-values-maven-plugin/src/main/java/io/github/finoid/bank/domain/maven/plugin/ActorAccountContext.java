package io.github.finoid.bank.domain.maven.plugin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.AccountTypesConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.ActorConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.Column;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.ColumnValueConverter;
import io.github.finoid.bank.domain.maven.plugin.parser.csv.RangeConverter;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ActorAccountContext {
    @Column(indice = 0, converter = @ColumnValueConverter(type = RangeConverter.class, context = "delimiter=-"))
    Range<Integer> accountNumber;

    @Column(indice = 1, converter = @ColumnValueConverter(type = ActorConverter.class))
    Actor actor;

    @Column(indice = 4, converter = @ColumnValueConverter(type = AccountTypesConverter.class, context = "delimiter=:"))
    AccountTypes accountType;

    public Type toPrimaryTypeOrThrow() {
        return accountType.primary();
    }

    public Type toSecondaryTypeOrThrow() {
        return accountType.secondary();
    }
}

package io.github.finoid.bank.domain;

import lombok.Value;

@Value
public class IntRange {
    int start;
    int end;

    public static IntRange of(final int start, final int end) {
        return new IntRange(start, end);
    }

    public boolean isWithinRange(final int number) {
        return number >= start && number <= end;
    }
}

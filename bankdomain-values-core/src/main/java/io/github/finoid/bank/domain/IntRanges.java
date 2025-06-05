package io.github.finoid.bank.domain;

import lombok.Value;

import java.util.Set;

@Value
public class IntRanges {
    Set<IntRange> ranges;

    public static IntRanges of(final IntRange... ranges) {
        return new IntRanges(CollectionUtils.orderedSet(ranges));
    }

    public boolean isWithinRange(final int number) {
        return ranges.stream()
            .anyMatch(range -> range.isWithinRange(number));
    }
}

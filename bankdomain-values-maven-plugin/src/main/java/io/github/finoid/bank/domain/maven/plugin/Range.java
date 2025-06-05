package io.github.finoid.bank.domain.maven.plugin;

import lombok.Value;

@Value
public class Range<T extends Number> {
    T start;
    T end;

    public static <T extends Number> Range<T> of(final T start, final T end) {
        return new Range<>(start, end);
    }

    public boolean isWithinRange(final T number) {
        return number.intValue() >= start.intValue()
            && number.intValue() <= end.intValue();
    }
}

package io.github.finoid.bank.domain.maven.plugin.util;

import lombok.experimental.UtilityClass;

import java.util.function.BinaryOperator;

@UtilityClass
public class StreamUtils {
    public static <T> BinaryOperator<T> toOne() {
        return (a, b) -> {
            throw new RuntimeException("Found more than one " + a.getClass().getSimpleName() + " #1 = " + a + " #2 = " + b);
        };
    }
}

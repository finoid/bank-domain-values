package io.github.finoid.bank.domain;

import lombok.experimental.UtilityClass;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@UtilityClass
public class ExceptionUtils {
    public static <T extends RuntimeException> void validOrThrow(final BooleanSupplier predicate, final Supplier<T> supplier) {
        if (!predicate.getAsBoolean()) {
            throw supplier.get();
        }
    }
}

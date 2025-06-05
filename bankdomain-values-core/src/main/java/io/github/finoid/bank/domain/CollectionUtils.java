package io.github.finoid.bank.domain;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@UtilityClass
public class CollectionUtils {
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Set<T> orderedSet(final T... elements) {
        final Set<T> s = new LinkedHashSet<>();
        Collections.addAll(s, elements);
        return Collections.unmodifiableSet(s);
    }
}

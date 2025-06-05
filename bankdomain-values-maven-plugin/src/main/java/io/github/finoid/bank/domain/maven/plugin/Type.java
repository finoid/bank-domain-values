package io.github.finoid.bank.domain.maven.plugin;

public enum Type {
    ONE, TWO, THREE, FOUR;

    @SuppressWarnings("array.access.unsafe.low")
    public static Type ofIndice(final int value) {
        final Type[] values = values();

        if (values.length < value) {
            throw new IllegalArgumentException("Invalid indice: " + value);
        }

        return values[value - 1];
    }
}
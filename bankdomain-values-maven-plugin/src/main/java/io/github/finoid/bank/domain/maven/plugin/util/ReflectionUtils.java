package io.github.finoid.bank.domain.maven.plugin.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;
import io.github.finoid.bank.domain.maven.plugin.exceptions.ReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@UtilityClass
public class ReflectionUtils {
    /**
     * Creates a new instance of {@code clazz}.
     * It must exist an accessibly non-args constructor in the class, otherwise this method will throw an exception.
     *
     * @param clazz the class
     * @param <T>   the class type
     * @return a new instance of clazz
     * @throws ReflectionException in case of an error
     */
    public static <T> T newInstance(final Class<T> clazz) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (final Exception e) {
            throw new ReflectionException(String.format("Unable to instantiate %s. Cause: %s", clazz, e.getMessage()), e);
        }
    }

    /**
     * Sets a field value on the given {@code instance}.
     *
     * @param instance the instance
     * @param field    the field to hold the value
     * @param value    the actual value
     * @param <T>      the instance type
     * @throws ReflectionException in case of an error
     */
    @SuppressWarnings("argument")
    public static <T> void setValue(final T instance, final Field field, @Nullable final Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (final Exception e) {
            throw new ReflectionException(String.format("Unable to set value for %s. Cause: %s", instance.getClass(), e.getMessage()), e);
        }
    }

    /**
     * Gets a field value from the given {@code instance}.
     *
     * @param instance the instance
     * @param field    the field to hold the value
     * @param <T>      the instance type
     * @throws ReflectionException in case of an error
     */
    public static <T> Object getValue(final T instance, final Field field) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (final Exception e) {
            throw new ReflectionException(String.format("Unable to get value from %s. Cause: %s", instance.getClass(), e.getMessage()), e);
        }
    }

    /**
     * Executes a method and returns the value from the given {@code instance}.
     *
     * @param instance the instance
     * @param method   the method to hold the value
     * @param <T>      the instance type
     * @throws ReflectionException in case of an error
     */
    public static <T> Object execute(final T instance, final Method method) {
        try {
            method.setAccessible(true);
            return method.invoke(instance);
        } catch (final Exception e) {
            throw new ReflectionException(String.format("Unable to execute method for %s. Cause: %s", instance.getClass(), e.getMessage()), e);
        }
    }
}

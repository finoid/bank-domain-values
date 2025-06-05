package io.github.finoid.bank.domain.internal;

/**
 * Annotates a program element (class, method, package, etc) which is internal to the library, not part of
 * the public API, and should not be used by users of bankdomain-values.
 *
 * <p>Note: This annotation is intended only for library code. Users should not attach this
 * annotation to their own code.
 */
public @interface Internal {
}

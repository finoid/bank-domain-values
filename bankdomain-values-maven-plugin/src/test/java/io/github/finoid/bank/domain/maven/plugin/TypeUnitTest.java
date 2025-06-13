package io.github.finoid.bank.domain.maven.plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TypeUnitTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void givenValidIndice_whenOfIndice_thenReturnsExpectedType(int index) {
        Type expected = Type.values()[index - 1];
        Assertions.assertEquals(expected, Type.ofIndice(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 5, 6})
    void givenInvalidIndice_whenOfIndice_thenThrowsIllegalArgumentException(int index) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Type.ofIndice(index));
    }
}

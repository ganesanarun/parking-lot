package org.sahaj.calculators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeTests {

    @ParameterizedTest(name = "{2}")
    @MethodSource(value = "invalidArguments")
    void throwAnExceptionWhenInvalidArgumentIsPassed(long from, long to, String testName) {
        assertThrows(IllegalArgumentException.class, () -> Range.from(from, to));
    }

    public static Stream<Arguments> invalidArguments() {
        return Stream.of(Arguments.of(-1, 10, "from is negative"),
            Arguments.of(0, -10, "to is negative"),
            Arguments.of(5, 2, "from should not be greater than to"),
            Arguments.of(1, 1, "from and to should not be equal"));
    }
}

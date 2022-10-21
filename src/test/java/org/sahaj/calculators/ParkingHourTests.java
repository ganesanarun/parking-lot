package org.sahaj.calculators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static java.time.ZonedDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingHourTests {

    @Test
    void throwExceptionWhenTryingToSubtractMoreThanAvailableHours() {
        assertThrows(ArithmeticException.class, () ->
            DataGenerator.from(10, 1).subtractHours(12));
    }

    @Test
    void returnEmptyParkingHourWhenSubtractedAllTheHours() {
        var response = DataGenerator.from(10, 1)
            .subtractHours(11);
        var response2 = DataGenerator.from(11, 0)
            .subtractHours(11);

        assertEquals(ParkingHour.empty(), response);
        assertEquals(ParkingHour.empty(), response2);
    }

    @Test
    void returnSubtractedParkingHour() {
        var response = DataGenerator.from(10, 1).subtractHours(10);
        var response2 = DataGenerator.from(10, 46).subtractHours(8);

        assertEquals(DataGenerator.from(0, 1), response);
        assertEquals(DataGenerator.from(2, 46), response2);
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("invalidDateArguments")
    void throwAnExceptionWhenDateInputIsInvalid(ZonedDateTime start, ZonedDateTime end, String message) {
        assertThrows(IllegalArgumentException.class, () -> ParkingHour.from(start, end));
    }

    static Stream<Arguments> invalidArguments() {
        return Stream.of(Arguments.of(-1, 50, "negative hours"),
            Arguments.of(1, -1, "negative minutes"),
            Arguments.of(1, 60, "minutes more than 59"));
    }

    static Stream<Arguments> invalidDateArguments() {
        return Stream.of(Arguments.of(null, now(), "null start"),
            Arguments.of(now(), null, "null end"),
            Arguments.of(now(), now().minusMinutes(5), "end is before start datetime"));
    }
}

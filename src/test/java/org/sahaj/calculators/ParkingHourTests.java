package org.sahaj.calculators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingHourTests {

    @ParameterizedTest(name = "{2}")
    @MethodSource("invalidArguments")
    void throwAnExceptionWhenInputIsInvalid(long hours, long minutes, String message) {
        assertThrows(IllegalArgumentException.class, () -> ParkingHour.from(hours, minutes));
    }

    @Test
    void throwExceptionWhenTryingToSubtractMoreThanAvailableHours() {
        assertThrows(ArithmeticException.class, () ->
            ParkingHour.from(10, 1).subtractHours(12));
    }

    @Test
    void returnEmptyParkingHourWhenSubtractedAllTheHours() {
       var response =    ParkingHour.from(10, 1)
           .subtractHours(11);
       var response2 =    ParkingHour.from(11, 0)
           .subtractHours(11);

       assertEquals(ParkingHour.empty(), response);
       assertEquals(ParkingHour.empty(), response2);
    }

    @Test
    void returnSubtractedParkingHour() {
       var response =    ParkingHour.from(10, 1)
           .subtractHours(10);
       var response2 =    ParkingHour.from(10, 46)
           .subtractHours(8);

       assertEquals(ParkingHour.from(0, 1), response);
       assertEquals(ParkingHour.from(2, 46), response2);
    }

    static Stream<Arguments> invalidArguments() {
        return Stream.of(Arguments.of(-1, 50, "negative hours"),
            Arguments.of(1, -1, "negative minutes"),
            Arguments.of(1, 60, "minutes more than 59"));
    }
}

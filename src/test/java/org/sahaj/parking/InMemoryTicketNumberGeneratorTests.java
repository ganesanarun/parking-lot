package org.sahaj.parking;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTicketNumberGeneratorTests {

    private static Stream<Arguments> initialInput() {
        return Stream.of(Arguments.of(1, todayDatePrefix() + "001", "with leading two zeros: 001"),
            Arguments.of(10, todayDatePrefix() + "010", "with one leading zero: 010"),
            Arguments.of(999, todayDatePrefix() + "999", "with no leading zero: 999"));
    }

    @ParameterizedTest(name = "[{index}] {2}")
    @MethodSource(value = "initialInput")
    void returnTicketNumber(int initialValue, String expectedOutput, String scenario) {
        final var generator = new InMemoryTicketNumberGenerator(initialValue,
            InMemoryTicketNumberGeneratorTests::todayDatePrefix);

        final var nextOne = generator.nextOne();

        assertEquals(expectedOutput, nextOne);
    }

    private static String todayDatePrefix() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMdd"));
    }
}

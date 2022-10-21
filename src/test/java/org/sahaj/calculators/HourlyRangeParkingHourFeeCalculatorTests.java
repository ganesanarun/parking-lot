package org.sahaj.calculators;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sahaj.Result;
import org.sahaj.Result.InvalidRangeError;
import org.sahaj.Result.Success;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HourlyRangeParkingHourFeeCalculatorTests {

    @ParameterizedTest(name = "{3}")
    @MethodSource("nullParameters")
    void throwAnExceptionWhenNextFeeProcessorIsNull(Range range,
        BigDecimal feeForDuration,
        ParkingHourFeeCalculator next,
        String message) {
        var exception = assertThrows(IllegalArgumentException.class,
            () -> HourlyRangeParkingHourFeeCalculator.ofWithIdentical(range, feeForDuration, next));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void returnFeeForRangeOfHours() {
        final var calculator = HourlyRangeParkingHourFeeCalculator.ofWithIdentical(
            Range.from(0, 5),
            BigDecimal.TEN,
            (totalHours) -> {
                throw new UnsupportedOperationException();
            });

        final Success<BigDecimal> result = (Success<BigDecimal>) calculator.calculate(ParkingHour.withHours(5));

        assertEquals(new Success<>(BigDecimal.valueOf(10)), result);
    }

    @Test
    void returnFailureResponseIfCalculatorIsNotSetupProperlyForRange() {
        var endRange = HourlyRangeParkingHourFeeCalculator.ofWithIdentical(
            Range.from(6, 10),
            BigDecimal.TEN,
            new EmptyHourFeeCalculator());
        final var calculator = HourlyRangeParkingHourFeeCalculator.ofWithIdentical(
            Range.from(0, 5),
            BigDecimal.TEN,
            endRange);

        var result = calculator.calculate(ParkingHour.withHours(6));

        assertInstanceOf(InvalidRangeError.class, result);
    }

    @Nested
    class RangeWithPerHourParkingFeeInTheEndTests {

        @ParameterizedTest(name = "Bike scenario {index} - {0} hours {1} minutes would cost {2}")
        @MethodSource(value = "bikeScenarios")
        void returnFeeWithRangeFeeModelWithFeePerHourInTheEnd(long hours, long minutes, long cost) {
            BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction = (parkingHour, range) -> parkingHour.subtractHours(
                range.getToHour());
            final var endRange = HourlyRangeParkingHourFeeCalculator.ofWithSum(Range.from(4, 12),
                BigDecimal.valueOf(60),
                parkingHourRangeParkingHourBiFunction,
                new FixedHourlyParkingHourFeeCalculator(BigDecimal.valueOf(100)));
            final var feeCalculator = HourlyRangeParkingHourFeeCalculator.ofWithSum(Range.from(0, 4),
                BigDecimal.valueOf(30),
                (parkingHour, range) -> parkingHour,
                endRange);
            final var parkingHour = ParkingHour.from(hours, minutes);

            final Success<BigDecimal> result = (Success<BigDecimal>) feeCalculator.calculate(parkingHour);

            assertEquals(new Success<>(BigDecimal.valueOf(cost)), result);
        }

        @ParameterizedTest(name = "SUV scenario {index} - {0} hours {1} minutes would cost {2}")
        @MethodSource(value = "suvScenarios")
        void returnFeeWithRangeFeeModelWithFeePerHourInTheEnd2(long hours, long minutes, long cost) {
            BiFunction<ParkingHour, Range, ParkingHour> parkingHourRangeParkingHourBiFunction = (parkingHour, range) -> parkingHour.subtractHours(
                range.getToHour());
            final var endRange = HourlyRangeParkingHourFeeCalculator.ofWithSum(Range.from(4, 12),
                BigDecimal.valueOf(120),
                parkingHourRangeParkingHourBiFunction,
                new FixedHourlyParkingHourFeeCalculator(BigDecimal.valueOf(200)));
            final var feeCalculator = HourlyRangeParkingHourFeeCalculator.ofWithSum(Range.from(0, 4),
                BigDecimal.valueOf(60),
                (parkingHour, range) -> parkingHour,
                endRange);
            final var parkingHour = ParkingHour.from(hours, minutes);

            final Success<BigDecimal> result = (Success<BigDecimal>) feeCalculator.calculate(parkingHour);

            assertEquals(new Success<>(BigDecimal.valueOf(cost)), result);
        }

        static Stream<Arguments> bikeScenarios() {
            return Stream.of(Arguments.of(14, 59, 390),
                Arguments.of(3, 40, 30));
        }

        static Stream<Arguments> suvScenarios() {
            return Stream.of(Arguments.of(11, 30, 180),
                Arguments.of(13, 5, 580));
        }
    }

    @Nested
    class RangeWithPerDayParkingFeeInTheEndTests {

        @ParameterizedTest(name = "Bike scenario {index} - {0} hours {1} minutes would cost {2}")
        @MethodSource(value = "bikeScenarios")
        void returnFeeWithRangeFeeModelWithFeePerHourInTheEnd(long hours, long minutes, long cost) {
            final var feeCalculator = HourlyRangeParkingHourFeeCalculator.ofWithIdentical(
                Range.from(0, 1),
                BigDecimal.valueOf(0),
                HourlyRangeParkingHourFeeCalculator.ofWithIdentical(Range.from(1, 8),
                    BigDecimal.valueOf(40),
                    HourlyRangeParkingHourFeeCalculator.ofWithIdentical(Range.from(8, 24),
                        BigDecimal.valueOf(60),
                        new PerDayParkingHourFeeCalculator(BigDecimal.valueOf(80)))));
            final var parkingHour = ParkingHour.from(hours, minutes);

            final Success<BigDecimal> result = (Success<BigDecimal>) feeCalculator.calculate(parkingHour);

            assertEquals(new Success<>(BigDecimal.valueOf(cost)), result);
        }

        @ParameterizedTest(name = "SUV scenario {index} - {0} hours {1} minutes would cost {2}")
        @MethodSource(value = "suvScenarios")
        void returnFeeWithRangeFeeModelWithFeePerHourInTheEnd2(long hours, long minutes, long cost) {
            final var feeCalculator = HourlyRangeParkingHourFeeCalculator.ofWithIdentical(
                Range.from(0, 12),
                BigDecimal.valueOf(60),
                HourlyRangeParkingHourFeeCalculator.ofWithIdentical(Range.from(12, 24),
                    BigDecimal.valueOf(80),
                    new PerDayParkingHourFeeCalculator(BigDecimal.valueOf(100))));
            final var parkingHour = ParkingHour.from(hours, minutes);

            final Success<BigDecimal> result = (Success<BigDecimal>) feeCalculator.calculate(parkingHour);

            assertEquals(new Success<>(BigDecimal.valueOf(cost)), result);
        }

        static Stream<Arguments> bikeScenarios() {
            return Stream.of(Arguments.of(0, 55, 0),
                Arguments.of(14, 59, 60),
                Arguments.of(36, 0, 160));
        }

        static Stream<Arguments> suvScenarios() {
            return Stream.of(Arguments.of(0, 50, 60),
                Arguments.of(23, 59, 80),
                Arguments.of(73, 0, 400));
        }
    }

    private record EmptyHourFeeCalculator() implements ParkingHourFeeCalculator {

        @Override
        public Result<BigDecimal> calculate(ParkingHour totalHours) {
            return null;
        }
    }

    private static Stream<Arguments> nullParameters() {
        return Stream.of(Arguments.of(Range.from(0, 2),
                BigDecimal.ONE,
                null,
                "Next parking fee calculator must not be null"),
            Arguments.of(null,
                BigDecimal.ONE,
                new EmptyHourFeeCalculator(),
                "Range must not be null"),
            Arguments.of(Range.from(0, 2),
                null,
                new EmptyHourFeeCalculator(),
                "Parking fee for duration must not be null"));
    }
}

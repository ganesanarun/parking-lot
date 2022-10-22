package org.sahaj.calculators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sahaj.common.FeeResult.Success;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(Lifecycle.PER_CLASS)
class PerDayParkingHourFeeCalculatorTests {

    @Test
    void returnOneDayParkingFeeWhenItsLessThan24Hours() {
        final var dailyFee = BigDecimal.valueOf(10);
        final var parkingHour = ParkingHour.withHours(12);
        final var calculator = new PerDayParkingHourFeeCalculator(dailyFee);

        final Success<BigDecimal> result = (Success<BigDecimal>) calculator.calculate(parkingHour);

        assertEquals(new Success<>(dailyFee), result);
    }

    @Test
    void returnTwoDayParkingFeeWhenItsMoreThan24HoursAndLessThan48Hours() {
        final var dailyFee = BigDecimal.valueOf(10);
        final var calculator = new PerDayParkingHourFeeCalculator(dailyFee);

        final Success<BigDecimal> result = (Success<BigDecimal>) calculator.calculate(ParkingHour.withHours(36));

        assertEquals(new Success<>(dailyFee.multiply(BigDecimal.valueOf(2))), result);
    }

    @ParameterizedTest(name = "{0} dailyFee for {1} hours would be {2} total fee")
    @MethodSource("parameters")
    void returnCorrectDailyFee(BigDecimal dailyFee, long totalHours, BigDecimal totalFee) {
        final var calculator = new PerDayParkingHourFeeCalculator(dailyFee);

        final Success<BigDecimal> result = (Success<BigDecimal>) calculator.calculate(ParkingHour.withHours(totalHours));

        assertEquals(new Success<>(totalFee), result);
    }

    Stream<Arguments> parameters() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(10), 72, BigDecimal.valueOf(30)),
            Arguments.of(BigDecimal.valueOf(100), 45, BigDecimal.valueOf(200)));
    }
}

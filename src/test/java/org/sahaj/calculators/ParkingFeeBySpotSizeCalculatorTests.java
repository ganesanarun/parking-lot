package org.sahaj.calculators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.sahaj.FeeResult.SpotSizeNotConfiguredError;
import org.sahaj.FeeResult.Success;
import org.sahaj.Size;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ParkingFeeBySpotSizeCalculatorTests {

    @ParameterizedTest(name = "[{index}] {0} not configured")
    @EnumSource(value = Size.class)
    void returnConfigurationMissingErrorWhenNotConfigured(Size size) {
        final var parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(Map.of());

        final var result = parkingFeeBySpotSizeCalculator.feeFor(new ParkingToken(ZonedDateTime.now(),
            ZonedDateTime.now().plus(2, ChronoUnit.DAYS),
            size));

        assertInstanceOf(SpotSizeNotConfiguredError.class, result);
    }

    @ParameterizedTest(name = "[{index}] {0} configured to return 10 for 1 hour/day")
    @EnumSource(value = Size.class)
    void returnFeeForTheParkingToken(Size size) {
        final var feeCalculatorMap = Map.of(Size.SMALL, new FixedHourlyParkingHourFeeCalculator(BigDecimal.TEN),
            Size.MEDIUM, new PerDayParkingHourFeeCalculator(BigDecimal.TEN),
            Size.LARGE, new PerDayParkingHourFeeCalculator(BigDecimal.TEN));
        final var parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(feeCalculatorMap);

        final var result = parkingFeeBySpotSizeCalculator.feeFor(new ParkingToken(ZonedDateTime.now(),
            ZonedDateTime.now().plus(59, ChronoUnit.MINUTES),
            size));

        final var success = assertInstanceOf(Success.class, result);
        assertEquals(BigDecimal.valueOf(10), success.value());
    }

    @ParameterizedTest
    @EnumSource(value = Size.class)
    void returnZeroFeeForTheParkingTokenWhenStartAndEndAreSame(Size size) {
        final var feeCalculatorMap = Map.of(Size.SMALL, new FixedHourlyParkingHourFeeCalculator(BigDecimal.TEN),
            Size.MEDIUM, new PerDayParkingHourFeeCalculator(BigDecimal.TEN),
            Size.LARGE, new PerDayParkingHourFeeCalculator(BigDecimal.TEN));
        final var parkingFeeBySpotSizeCalculator = new ParkingFeeBySpotSizeCalculator(feeCalculatorMap);
        final var start = ZonedDateTime.now();
        final var token = new ParkingToken(start, start, size);

        final var result = parkingFeeBySpotSizeCalculator.feeFor(token);

        final var success = assertInstanceOf(Success.class, result);
        assertEquals(BigDecimal.valueOf(0), success.value());
    }
}

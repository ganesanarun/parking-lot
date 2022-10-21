package org.sahaj.calculators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sahaj.Result.Success;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FixedHourlyParkingHourFeeCalculatorTests {

    @Test
    void returnFeePerHour() {
        final var fixedHourlyParkingFeeCalculator = new FixedHourlyParkingHourFeeCalculator(BigDecimal.valueOf(10));
        var parkingHour = ParkingHour.withHours(10);

        var result = fixedHourlyParkingFeeCalculator.calculate(parkingHour);

        final Success<BigDecimal> success = Assertions.assertInstanceOf(Success.class, result);
        assertEquals(new Success<>(BigDecimal.valueOf(100)), success);
    }

}

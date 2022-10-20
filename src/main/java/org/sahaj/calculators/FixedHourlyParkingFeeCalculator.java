package org.sahaj.calculators;

import org.sahaj.Result;
import org.sahaj.Result.Success;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static org.sahaj.calculators.ParkingHour.roundUpHours;

public class FixedHourlyParkingFeeCalculator implements ParkingFeeCalculator {

    private final BigDecimal feePerHour;

    public FixedHourlyParkingFeeCalculator(BigDecimal feePerHour) {
        this.feePerHour = feePerHour;
    }

    @Override
    public Result<BigDecimal> calculate(ParkingHour parkingHour) {
        return new Success<>(feePerHour.multiply(valueOf(roundUpHours(parkingHour))));
    }
}
